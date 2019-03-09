package com.financeManager.demo.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.controllers.Helper;
import com.financeManager.demo.dao.IBudgetDAO;
import com.financeManager.demo.dto.AmountOverviewDTO;
import com.financeManager.demo.dto.BudgetOverviewDTO;
import com.financeManager.demo.dto.CategoryOverviewDTO;
import com.financeManager.demo.dto.PercentageOfCategoryAmountDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidAmountsEntryException;
import com.financeManager.demo.exceptions.InvalidDateException;
import com.financeManager.demo.exceptions.InvalidTransactionTypeException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.model.Budget;
import com.financeManager.demo.model.Category;
import com.financeManager.demo.model.Transaction;
import com.financeManager.demo.model.User;
import com.financeManager.demo.repositories.ITransactionRepository;
import com.financeManager.demo.repositories.IUsersRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticService {

	private static final String NOW = "Now";
	private static final String BEGINNING = "Beginning";
	private static final int COEFF_FORMATING_EXPENSES = -1;
	private static final Double ZERO = new Double(0);
	private static final String INCOME = "income";
	private static final String EXPENSE = "expense";

	@Autowired
	private ITransactionRepository transactionRepo;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private BudgetService budgetService;

	@Autowired
	private IUsersRepository userRepo;

	@Autowired
	private IBudgetDAO budgetDao;

	private DecimalFormat df = new DecimalFormat("##.##%");

	public AmountOverviewDTO getOverviewOfUserActivity(User user, String from, String till)
			throws DateFormatException, InvalidDateException {

		Timestamp startDate = Helper.parseStringToTimeStamp(from);
		Timestamp endDate = Helper.parseStringToTimeStamp(till);

		List<Transaction> transactions = this.checkParams(user, startDate, endDate);

		Double incomes = sumIncomes(transactions);
		Double expenses = sumExpenses(transactions);

		AmountOverviewDTO overview = new AmountOverviewDTO();

		overview.setFrom(from != null ? from : BEGINNING);
		overview.setTill(till != null ? till : NOW);
		overview.setIncomes(incomes);
		overview.setExpenses(expenses);
		overview.setSavings(incomes - expenses > 0 ? incomes - expenses : 0);
		overview.setOverruns(expenses - incomes > 0 ? expenses - incomes : 0);

		return overview;
	}

	public CategoryOverviewDTO getOverviewOfUserActivityByCategories(User user, String from, String till, String type)
			throws DateFormatException, InvalidDateException, InvalidTransactionTypeException {

		Timestamp startDate = Helper.parseStringToTimeStamp(from);
		Timestamp endDate = Helper.parseStringToTimeStamp(till);

		List<Transaction> transactions = this.checkParams(user, startDate, endDate);

		if (!type.equals(INCOME) && !type.equals(EXPENSE)) {
			throw new InvalidTransactionTypeException("Invalid transaction type! Input: income/expense");
		}

		transactions = transactions.stream().filter(
				transaction -> transaction.getCategory().getTransactionType().getName().equals(type.toLowerCase()))
				.collect(Collectors.toList());

		Map<Category, List<Transaction>> mappedByCategory = mapTransactionByCategory(transactions);

		Map<Category, Double> amountsByCategory = sumAmountOfCategory(mappedByCategory, type);

		List<PercentageOfCategoryAmountDTO> listByCategories = makePercentageByCategoryDTOS(mappedByCategory,
				amountsByCategory);

		CategoryOverviewDTO overview = new CategoryOverviewDTO();

		overview.setFrom(from != null ? from : BEGINNING);
		overview.setTill(till != null ? till : NOW);
		overview.setCategoriesOverview(listByCategories);

		return overview;
	}

	private List<PercentageOfCategoryAmountDTO> makePercentageByCategoryDTOS(
			Map<Category, List<Transaction>> mappedByCategory, Map<Category, Double> amountsByCategory) {
		Double amountOfAll = ZERO;

		for (Entry<Category, Double> entry : amountsByCategory.entrySet()) {
			amountOfAll += entry.getValue();
		}

		List<PercentageOfCategoryAmountDTO> listByCategories = new LinkedList<PercentageOfCategoryAmountDTO>();

		for (Entry<Category, Double> entry : amountsByCategory.entrySet()) {
			PercentageOfCategoryAmountDTO dto = new PercentageOfCategoryAmountDTO();

			dto.setCategory(entry.getKey().getName());
			dto.setAmount(entry.getValue());
			dto.setTransactionCount(mappedByCategory.get(entry.getKey()).size());
			dto.setPercentageOfTotal(df.format(entry.getValue() / amountOfAll));

			listByCategories.add(dto);

		}

		return listByCategories;
	}

	private Map<Category, Double> sumAmountOfCategory(Map<Category, List<Transaction>> mappedByCategory, String type) {
		Map<Category, Double> amountsByCategory = new HashMap<Category, Double>();

		for (Entry<Category, List<Transaction>> entry : mappedByCategory.entrySet()) {

			if (type.equals(INCOME)) {
				amountsByCategory.put(entry.getKey(), sumIncomes(entry.getValue()));
			} else {
				amountsByCategory.put(entry.getKey(), sumExpenses(entry.getValue()));
			}
		}

		return amountsByCategory;
	}

	private Map<Category, List<Transaction>> mapTransactionByCategory(List<Transaction> transactions) {
		Map<Category, List<Transaction>> mappedByCategory = new HashMap<Category, List<Transaction>>();

		transactions.forEach(transaction -> {
			if (!mappedByCategory.containsKey(transaction.getCategory())) {
				mappedByCategory.put(transaction.getCategory(), new LinkedList<Transaction>());
			}

			mappedByCategory.get(transaction.getCategory()).add(transaction);
		});

		return mappedByCategory;
	}

	private List<Transaction> checkParams(User user, Timestamp startDate, Timestamp endDate)
			throws InvalidDateException {
		if (startDate == null && endDate == null) {
			return this.transactionRepo.findAllTransactionsByUser(user);
		}

		if (startDate == null && endDate != null) {
			return this.transactionRepo.findAllTransactionsByUserAndCreationDateIsBefore(user, endDate);
		}

		if ((startDate != null && endDate != null) && startDate.before(endDate)) {
			throw new InvalidDateException("Invalid data input!");
		}

		if (startDate != null && endDate == null) {
			return this.transactionRepo.findAllTransactionsByUserAndCreationDateIsAfter(user, startDate);
		}

		if (startDate == endDate) {
			return this.transactionRepo.findAllTransactionsByUserAndCreationDateIsEquals(user, startDate);
		}

		if (startDate != null && endDate != null) {
			return this.transactionRepo.findAllTransactionsByUserAndCreationDateIsBetween(user, startDate, endDate);
		}

		return null;
	}

	private Double sumExpenses(List<Transaction> transactions) {
		Double sumOfExpenses = ZERO;

		Optional<Double> expenses = transactions.stream()
				.filter(transaction -> transaction.getCategory().getTransactionType().getName().equals(EXPENSE))
				.map(transaction -> transaction.getAmount() * COEFF_FORMATING_EXPENSES)
				.reduce((sum, amount) -> sum + amount);

		if (expenses.isPresent()) {
			sumOfExpenses = expenses.get();
		}

		return sumOfExpenses;
	}

	private Double sumIncomes(List<Transaction> transactions) {
		Double sumOfIncomes = ZERO;

		Optional<Double> incomes = transactions.stream()
				.filter(transaction -> transaction.getCategory().getTransactionType().getName().equals(INCOME))
				.map(transaction -> transaction.getAmount()).reduce((sum, amount) -> sum + amount);

		if (incomes.isPresent()) {
			sumOfIncomes = incomes.get();
		}

		return sumOfIncomes;
	}

	public BudgetOverviewDTO getBudgetMovement(Long budgetId, Long userId) throws NotExistingBudgetException,
			ForbiddenException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		Budget budget = null;
		try {
			budget = this.budgetDao.getBudgetById(budgetId);
		} catch (NotExistingBudgetException e) {
			e.printStackTrace();
			throw new NotExistingBudgetException("Budget doesn't exists");
		}

		if (!userId.equals(budget.getUser().getId())) {
			throw new ForbiddenException("You are not allowed to view this budget!");
		}

		User user = this.userRepo.findById(userId).get();

		Date startDate = this.budgetService.getBudgetById(userId, budgetId).getStartDate();
		Date endDate = this.budgetService.getBudgetById(userId, budgetId).getEndDate();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sDate = df.format(startDate);
		String eDate = df.format(endDate);

		List<TransactionDTO> dtos = this.transactionService.getAllTransactionsOfUserForGivenCategory(user, null, null,
				null, null, sDate, eDate, budget.getCategory().getId());

		BudgetOverviewDTO budgetOverview = new BudgetOverviewDTO();

		budgetOverview.setBudgetId(budgetId);
		budgetOverview.setBudgetAmount(budget.getAmount());
		budgetOverview.setCategoryName(budget.getCategory().getName());
		budgetOverview.setPeriodOfBudget(sDate + " - " + eDate);
		budgetOverview.setTransactionCount(dtos.size());

		Double sum = dtos.stream().map(dto -> dto.getAmount()).reduce((double) 0, (d1, d2) -> d1 + d2);

		budgetOverview.setMoneySpent(sum);
		budgetOverview.setFinalCalculation(budget.getAmount() - sum);
		return budgetOverview;
	}

}
