package com.financeManager.demo.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.controllers.Helper;
import com.financeManager.demo.dao.IBudgetDAO;
import com.financeManager.demo.dao.IRepeatPeriodDAO;
import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.AmountOverviewDTO;
import com.financeManager.demo.dto.BestAndWorseMonthOverviewDTO;
import com.financeManager.demo.dto.BudgetOverviewDTO;
import com.financeManager.demo.dto.CategoryOverviewDTO;
import com.financeManager.demo.dto.CategoryWithMostExpensesDTO;
import com.financeManager.demo.dto.DayActivityDTO;
import com.financeManager.demo.dto.PercentageOfCategoryAmountDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.dto.WalletSummaryDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidAmountsEntryException;
import com.financeManager.demo.exceptions.InvalidDateException;
import com.financeManager.demo.exceptions.InvalidPeriodException;
import com.financeManager.demo.exceptions.InvalidTransactionTypeException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.Budget;
import com.financeManager.demo.model.Category;
import com.financeManager.demo.model.Transaction;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;
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
	private static final int EXPENSE_TRANSACTIONS_ID = 2;
	private static final String EXCEEDED = "Exceeded";
	private static final String NOT_EXCEEDED = "Not exceeded";
	private static final String PENDING = "Pending";
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

	@Autowired
	private IWalletDAO walletDao;

	@Autowired
	private IRepeatPeriodDAO repeatPeriodDao;

	@Autowired
	private UserService userService;

	private DecimalFormat df = new DecimalFormat("##.##%");

	public AmountOverviewDTO getOverviewOfUserActivity(Long userId, String from, String till)
			throws DateFormatException, InvalidDateException, NotExistingUserException {

		Timestamp startDate = Helper.parseStringToTimeStamp(from);
		Timestamp endDate = Helper.parseStringToTimeStamp(till);

		User user = this.userService.getExistingUserById(userId);

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

	public CategoryOverviewDTO getOverviewOfUserActivityByCategories(Long userId, String from, String till, String type)
			throws DateFormatException, InvalidDateException, InvalidTransactionTypeException,
			NotExistingUserException {

		Timestamp startDate = Helper.parseStringToTimeStamp(from);
		Timestamp endDate = Helper.parseStringToTimeStamp(till);

		User user = this.userService.getExistingUserById(userId);

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

		if ((startDate != null && endDate != null) && startDate.after(endDate)) {
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
		Budget budget = this.budgetDao.getBudgetById(budgetId);

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
		String status = null;
		if (budgetOverview.getFinalCalculation() > 0) {
			status = NOT_EXCEEDED;
		} else {
			status = EXCEEDED;
		}
		budgetOverview.setStatus(status);

		return budgetOverview;
	}

	public List<BudgetOverviewDTO> getOverviewForAllBudgets(Long userId) {

		List<Budget> budgets = this.budgetDao.getAllUserBudgets(userId);

		return budgets.stream().map(budget -> {

			User user = this.userRepo.findById(userId).get();

			Date startDate = budget.getStartDate();
			Date endDate = budget.getEndDate();

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String sDate = df.format(startDate);
			String eDate = df.format(endDate);

			List<TransactionDTO> dtos = null;
			try {
				dtos = this.transactionService.getAllTransactionsOfUserForGivenCategory(user, null, null, null, null,
						sDate, eDate, budget.getCategory().getId());
			} catch (InvalidAmountsEntryException | InvalidDateException | DateFormatException e) {
				e.printStackTrace();
			}

			BudgetOverviewDTO budgetOverview = new BudgetOverviewDTO();

			budgetOverview.setBudgetId(budget.getId());
			budgetOverview.setBudgetAmount(budget.getAmount());
			budgetOverview.setCategoryName(budget.getCategory().getName());
			budgetOverview.setPeriodOfBudget(sDate + " - " + eDate);
			Double sum = new Double(0);

			if (dtos != null) {
				budgetOverview.setTransactionCount(dtos.size());
				sum = dtos.stream().map(dto -> dto.getAmount()).reduce((double) 0, (d1, d2) -> d1 + d2);
			} else {
				budgetOverview.setTransactionCount(0);
			}

			budgetOverview.setMoneySpent(sum);
			budgetOverview.setFinalCalculation(budget.getAmount() - sum);
			String status = null;

			if (budgetOverview.getFinalCalculation() > 0) {
				status = NOT_EXCEEDED;
			} else {
				status = EXCEEDED;
			}

			if (budget.getIsDeleted() == 0) {
				status = PENDING;
			}
			budgetOverview.setStatus(status);
			return budgetOverview;
		}).collect(Collectors.toList());

	}

	public WalletSummaryDTO getSummaryOfWallet(Long userId, Long walletId)
			throws NotExistingWalletException, ForbiddenException {

		Wallet wallet = this.walletDao.getWalletById(walletId);

		if (!userId.equals(wallet.getUser().getId())) {
			throw new ForbiddenException("You are not allowed to view this wallet!");
		}

		List<Transaction> transactionsOfWallet = this.transactionRepo.findAllByWalletId(walletId);

		WalletSummaryDTO walletSummary = new WalletSummaryDTO();
		walletSummary.setId(walletId);
		walletSummary.setTransactionCount(transactionsOfWallet.size());
		walletSummary.setName(wallet.getName());
		walletSummary.setBalance(wallet.getBalance());

		double sumOfIncomes = transactionsOfWallet.stream().map(transaction -> transaction.getAmount())
				.filter(amount -> amount > 0).reduce((double) 0, (amount1, amount2) -> amount1 + amount2);

		double sumOfExpenses = transactionsOfWallet.stream()
				.map(transaction -> transaction.getAmount() * COEFF_FORMATING_EXPENSES).filter(amount -> amount > 0)
				.reduce((double) 0, (amount1, amount2) -> amount1 + amount2);
		walletSummary.setTotalMoneyPayed(sumOfExpenses);
		walletSummary.setTotalMoneyReceived(sumOfIncomes);

		return walletSummary;
	}

	public List<WalletSummaryDTO> getAllWalletsSummary(Long userId) {

		List<Wallet> walletsOfUser = this.walletDao.getAllUserWallets(userId);

		return walletsOfUser.stream().map(wallet -> {
			try {
				return this.getSummaryOfWallet(userId, wallet.getId());
			} catch (NotExistingWalletException e) {
				e.printStackTrace();
			} catch (ForbiddenException e) {
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());

	}
	
	public CategoryWithMostExpensesDTO getMostSpendingCategory(Long userId,String from,String till) throws NotExistingUserException, InvalidDateException, DateFormatException {
		User user = null;
		try {
		 user = this.userRepo.findById(userId).get();
		}catch(NoSuchElementException e) {
			throw new NotExistingUserException("User doesn't exists");
		}
		
		List<Transaction> transactions = this.transactionRepo.findAllTransactionsByUser(user);
		
		if(transactions.size() == 0) {
			return new CategoryWithMostExpensesDTO("You have no transactions", (double) 0);
		}
		
		Timestamp startDate = Helper.parseStringToTimeStamp(from);	
		Timestamp endDate = Helper.parseStringToTimeStamp(till);	
		transactions = this.checkParams(user, startDate, endDate);
		
		
		Map<String,List<Double>> typeToSum = new HashMap<String,List<Double>>();
		

		transactions = transactions.stream().map(transaction -> {
		
			
			if(transaction.getCategory().getTransactionType().getId() == EXPENSE_TRANSACTIONS_ID) {
			if(!typeToSum.containsKey(transaction.getCategory().getName())) {
				typeToSum.put(transaction.getCategory().getName(), new LinkedList<Double>());		

			}

			typeToSum.get(transaction.getCategory().getName()).add(transaction.getAmount() * COEFF_FORMATING_EXPENSES);			
			}	
			return transaction;
		}).collect(Collectors.toList());
		
		double maxSum = 0f;

		String nameOfCat = null;
		
		for(Entry<String,List<Double>> categories : typeToSum.entrySet()) {
			double sumOfCategory = categories.getValue().stream().reduce(new Double(0),(t1,t2)-> t1+t2);
	
			
			if(sumOfCategory > maxSum) {
				maxSum = sumOfCategory;
				nameOfCat = categories.getKey();
			}
		}
		return new CategoryWithMostExpensesDTO(nameOfCat,maxSum);
	}

	public List<DayActivityDTO> getOverviewOfDayActivity(Long userId, String period)
			throws NotExistingUserException, InvalidDateException, InvalidPeriodException {

		Timestamp startDate = this.repeatPeriodDao.calculateStartDateByPeriod(period);
		Timestamp endDate = Timestamp.valueOf(LocalDateTime.now());

		User user = this.userService.getExistingUserById(userId);

		List<Transaction> transactions = this.checkParams(user, startDate, endDate);

		List<TransactionDTO> transactionDTOS = transactions.stream()
				.map(transaction -> this.transactionService.convertFromTransactionToTransactionDTO(transaction))
				.collect(Collectors.toList());

		Map<LocalDate, List<TransactionDTO>> mappedByDays = mapTransactionsByCreationDate(transactionDTOS);

		return dayActivityResolve(mappedByDays);
	}

	private List<DayActivityDTO> dayActivityResolve(Map<LocalDate, List<TransactionDTO>> mappedByDays) {
		List<DayActivityDTO> listDayActivity = new LinkedList<DayActivityDTO>();

		for (Entry<LocalDate, List<TransactionDTO>> entry : mappedByDays.entrySet()) {
			DayActivityDTO activity = new DayActivityDTO();

			activity.setDay(entry.getKey().toString());

			List<TransactionDTO> incomes = entry.getValue().stream()
					.filter(transaction -> transaction.getTransactionType().equals(INCOME))
					.collect(Collectors.toList());
			activity.setIncomeCount(incomes.size());
			activity.setIncomeSum(this.sumIncomeInfo(incomes));

			List<TransactionDTO> expenses = entry.getValue().stream()
					.filter(transaction -> transaction.getTransactionType().equals(EXPENSE))
					.collect(Collectors.toList());
			activity.setExpenseCount(expenses.size());
			activity.setExpenseSum(this.sumExpenseInfo(expenses));

			activity.setTransactions(entry.getValue());

			listDayActivity.add(activity);
		}

		return listDayActivity;
	}

	private Double sumExpenseInfo(List<TransactionDTO> expenses) {

		Double sumOfExpenses = ZERO;

		Optional<Double> result = expenses.stream().map(transaction -> transaction.getAmount())
				.reduce((sum, amount) -> sum + amount);

		if (result.isPresent()) {
			sumOfExpenses = result.get();
		}

		return sumOfExpenses;
	}

	private Double sumIncomeInfo(List<TransactionDTO> incomes) {
		Double sumOfIncomes = ZERO;

		Optional<Double> result = incomes.stream().map(transaction -> transaction.getAmount())
				.reduce((sum, amount) -> sum + amount);

		if (result.isPresent()) {
			sumOfIncomes = result.get();
		}

		return sumOfIncomes;
	}

	private Map<LocalDate, List<TransactionDTO>> mapTransactionsByCreationDate(List<TransactionDTO> transactionDTOS) {
		Map<LocalDate, List<TransactionDTO>> mappedByDays = new HashMap<LocalDate, List<TransactionDTO>>();

		for (TransactionDTO transaction : transactionDTOS) {

			LocalDate date = transaction.getCreationDate().toLocalDate();

			if (!mappedByDays.containsKey(date)) {
				mappedByDays.put(date, new LinkedList<TransactionDTO>());
			}

			mappedByDays.get(date).add(transaction);

		}

		return mappedByDays;
	}

	public BestAndWorseMonthOverviewDTO getBestAndWorseMonthOverview(Long userId) throws NotExistingUserException {

		User user = this.userService.getExistingUserById(userId);

		List<Transaction> transactions = this.transactionRepo.findAllTransactionsByUser(user);

		System.out.println(transactions.size());

		Map<Integer, Map<String, List<Transaction>>> transactionsMappedByYearAndMonth = mapTransactionsByYearAndMonth(
				transactions);

		Map<Integer, Map<String, Double>> incomesMappedByYearAndMonth = mapIncomesByYearAndMonth(
				transactionsMappedByYearAndMonth);

		Map<Integer, Map<String, Double>> expensesMappedByYearAndMonth = mapExpensesByYearAndMonth(
				transactionsMappedByYearAndMonth);

		Integer bestYear = null;
		String bestMonth = null;
		Double maxSum = ZERO;

		Integer worstYear = null;
		String worstMonth = null;
		Double minSum = ZERO;

		for (Entry<Integer, Map<String, Double>> yearEntry : incomesMappedByYearAndMonth.entrySet()) {

			Map<String, Double> monthMap = yearEntry.getValue();

			System.out.println(yearEntry.getValue().size());

			for (Entry<String, Double> month : monthMap.entrySet()) {

				if (month.getValue() > maxSum) {

					maxSum = month.getValue();
					bestMonth = month.getKey();
					bestYear = yearEntry.getKey();
				}

				System.out.println(month.getKey());
			}
		}

		for (Entry<Integer, Map<String, Double>> yearEntry : expensesMappedByYearAndMonth.entrySet()) {
			for (Entry<String, Double> monthEntry : yearEntry.getValue().entrySet()) {

				if (monthEntry.getValue() > minSum) {

					minSum = monthEntry.getValue();
					worstMonth = monthEntry.getKey();
					worstYear = yearEntry.getKey();
				}
			}
		}

		BestAndWorseMonthOverviewDTO overview = new BestAndWorseMonthOverviewDTO();
		overview.setBestMonth(bestMonth != null ? bestMonth + " " + bestYear : "");
		overview.setMonthIncome(maxSum);
		overview.setWorstMonth(worstMonth != null ? worstMonth + " " + worstYear : "");
		overview.setMonthExpense(minSum * COEFF_FORMATING_EXPENSES);
		return overview;
	}

	private Map<Integer, Map<String, Double>> mapIncomesByYearAndMonth(
			Map<Integer, Map<String, List<Transaction>>> transactionsMappedByYearAndMonth) {
		Map<Integer, Map<String, Double>> sumMappedByYearAndMonth = new HashMap<Integer, Map<String, Double>>();

		for (Entry<Integer, Map<String, List<Transaction>>> yearEntry : transactionsMappedByYearAndMonth.entrySet()) {

			if (!sumMappedByYearAndMonth.containsKey(yearEntry.getKey())) {
				sumMappedByYearAndMonth.put(yearEntry.getKey(), new HashMap<String, Double>());
			}

			Map<String, Double> month = sumMappedByYearAndMonth.get(yearEntry.getKey());

			for (Entry<String, List<Transaction>> monthEntry : yearEntry.getValue().entrySet()) {

				if (!month.containsKey(monthEntry.getKey())) {
					month.put(monthEntry.getKey(), ZERO);
				}

				Double sumOfMonth = monthEntry.getValue().stream().filter(transaction -> transaction.getAmount() > ZERO)
						.map(transaction -> transaction.getAmount()).reduce(ZERO, (sum, amount) -> sum + amount);

				month.put(monthEntry.getKey(), sumOfMonth);
			}
		}

		return sumMappedByYearAndMonth;
	}

	private Map<Integer, Map<String, Double>> mapExpensesByYearAndMonth(
			Map<Integer, Map<String, List<Transaction>>> transactionsMappedByYearAndMonth) {
		Map<Integer, Map<String, Double>> sumMappedByYearAndMonth = new HashMap<Integer, Map<String, Double>>();

		for (Entry<Integer, Map<String, List<Transaction>>> yearEntry : transactionsMappedByYearAndMonth.entrySet()) {

			if (!sumMappedByYearAndMonth.containsKey(yearEntry.getKey())) {
				sumMappedByYearAndMonth.put(yearEntry.getKey(), new HashMap<String, Double>());
			}

			Map<String, Double> month = sumMappedByYearAndMonth.get(yearEntry.getKey());

			for (Entry<String, List<Transaction>> monthEntry : yearEntry.getValue().entrySet()) {

				if (!month.containsKey(monthEntry.getKey())) {
					month.put(monthEntry.getKey(), ZERO);
				}

				Double sumOfMonth = monthEntry.getValue().stream().filter(transaction -> transaction.getAmount() < ZERO)
						.map(transaction -> transaction.getAmount() * COEFF_FORMATING_EXPENSES)
						.reduce(ZERO, (sum, amount) -> sum + amount);

				month.put(monthEntry.getKey(), sumOfMonth);
			}
		}

		return sumMappedByYearAndMonth;
	}

	private Map<Integer, Map<String, List<Transaction>>> mapTransactionsByYearAndMonth(List<Transaction> transactions) {
		Map<Integer, Map<String, List<Transaction>>> mappedByYearAndMonth = new HashMap<Integer, Map<String, List<Transaction>>>();

		for (Transaction transaction : transactions) {
			Integer year = transaction.getCreationDate().toLocalDateTime().getYear();
			String month = transaction.getCreationDate().toLocalDateTime().getMonth().toString();

			if (!mappedByYearAndMonth.containsKey(year)) {
				mappedByYearAndMonth.put(year, new HashMap<String, List<Transaction>>());
			}

			if (!mappedByYearAndMonth.get(year).containsKey(month)) {
				mappedByYearAndMonth.get(year).put(month, new LinkedList<Transaction>());
			}

			mappedByYearAndMonth.get(year).get(month).add(transaction);
		}

		return mappedByYearAndMonth;
	}
	
	public double getAverageMoneyPerDay(Long userId, int period) {
		List<Wallet> wallets = this.walletDao.getAllUserWallets(userId);
		double sum = wallets.stream().map(wallet -> wallet.getBalance()).reduce(new Double(0),(w1,w2) ->w1+w2);
		return sum / period;
	}

}
