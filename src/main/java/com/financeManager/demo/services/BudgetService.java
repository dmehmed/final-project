package com.financeManager.demo.services;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.IBudgetDAO;
import com.financeManager.demo.dao.ICategoryDao;
import com.financeManager.demo.dao.IRepeatPeriodDAO;
import com.financeManager.demo.dto.BudgetDTO;
import com.financeManager.demo.dto.BudgetOverviewDTO;
import com.financeManager.demo.dto.CrudBudgetDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.exceptions.AlreadyExistingBudget;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidAmountsEntryException;
import com.financeManager.demo.exceptions.InvalidBudgetEntryException;
import com.financeManager.demo.exceptions.InvalidDateException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.model.Budget;
import com.financeManager.demo.model.Category;
import com.financeManager.demo.model.RepeatPeriod;
import com.financeManager.demo.model.User;
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
public class BudgetService {

	private static final long ID_OF_DEFAULT_REPEAT_PERIOD = 1;
	@Autowired
	private IBudgetDAO budgetDao;
	@Autowired
	private IUsersRepository usersRepo;
	@Autowired
	private ICategoryDao categoryDao;
	@Autowired
	private IRepeatPeriodDAO repeatPeriodsDao;
	@Autowired
	private TransactionService transactionService;

	public List<BudgetDTO> getAllUserBugdets(Long userId) {
		List<Budget> budgets = this.budgetDao.getActiveUserBudgets(userId);

		if (budgets == null) {
			return new LinkedList<BudgetDTO>();
		}

		return budgets.stream()
				.map(budget -> new BudgetDTO(budget.getId(), budget.getAmount(), budget.getStartDate(),
						budget.getEndDate(), this.categoryDao.getById(budget.getCategory().getId()).getName(),
						this.repeatPeriodsDao.getById(budget.getRepeatPeriod().getId()).getPeriod()))
				.collect(Collectors.toList());

	}

	public Long addBudgetToUser(CrudBudgetDTO newBudget, Long userId) throws InvalidBudgetEntryException, AlreadyExistingBudget {
		User owner = this.usersRepo.findById(userId).get();

		if (newBudget.getAmount() == null) {
			throw new InvalidBudgetEntryException("Invalid budget amount!");
		}

		if (newBudget.getCategoryId() == null) {
			throw new InvalidBudgetEntryException("You must choose category");
		}

		try {

			if (newBudget.getRepeatPeriodId() == null) {
				newBudget.setRepeatPeriodId(ID_OF_DEFAULT_REPEAT_PERIOD);
			}

			Category category = this.categoryDao.getById(newBudget.getCategoryId());
			RepeatPeriod repeatPeriod = this.repeatPeriodsDao.getById(newBudget.getRepeatPeriodId());

			Date startDate = Date.valueOf(LocalDate.now());
			Date endDate = this.repeatPeriodsDao.calculateEndDateByPeriod(repeatPeriod.getId());

			Budget budget = new Budget(newBudget.getAmount(), startDate, endDate, owner, category, repeatPeriod);
			this.budgetDao.addBudget(budget);
			return budget.getId();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new InvalidBudgetEntryException("Bad input!");
		}

	}

	public BudgetDTO getBudgetById(Long userId, Long budgetId) throws NotExistingBudgetException, ForbiddenException {

		Budget budget = this.budgetDao.getBudgetById(budgetId);
		if (!userId.equals(budget.getUser().getId())) {
			throw new ForbiddenException("You are not allowed to see this budget!");
		}

		return new BudgetDTO(budget.getId(), budget.getAmount(), budget.getStartDate(), budget.getEndDate(),
				budget.getCategory().getName(), budget.getRepeatPeriod().getPeriod());

	}

	public void updateBudget(CrudBudgetDTO budgetUpdater, Long userId, Long id)
			throws NotExistingBudgetException, InvalidBudgetEntryException, ForbiddenException {
		Budget budget = null;
		try {
			budget = this.budgetDao.getBudgetById(id);
		} catch (NotExistingBudgetException e) {
			throw new NotExistingBudgetException("Budget doesn't exists");
		}

		if (!userId.equals(budget.getUser().getId())) {
			throw new ForbiddenException("You are not allowed to update this budget!");
		}

		if (budgetUpdater.getAmount() != null) {
			this.budgetDao.getBudgetById(id).setAmount(budgetUpdater.getAmount());
		}

		try {

			if (budgetUpdater.getCategoryId() != null) {
				this.budgetDao.getBudgetById(id).setCategory(this.categoryDao.getById(budgetUpdater.getCategoryId()));
			}
			if (budgetUpdater.getRepeatPeriodId() != null) {
				this.budgetDao.getBudgetById(id)
						.setRepeatPeriod(this.repeatPeriodsDao.getById(budgetUpdater.getRepeatPeriodId()));
				this.budgetDao.getBudgetById(id).setStartDate(Date.valueOf(LocalDate.now()));
				this.budgetDao.getBudgetById(id)
						.setEndDate(this.repeatPeriodsDao.calculateEndDateByPeriod(budgetUpdater.getRepeatPeriodId()));
			}

			this.budgetDao.saveUpdatedBudget(id);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new InvalidBudgetEntryException("Bad update budget input!");
		}

	}

	public void deleteBudgetById(Long userId, Long budgetId) throws NotExistingBudgetException, ForbiddenException {

		Budget budget = null;
		try {
			budget = this.budgetDao.getBudgetById(budgetId);
		} catch (NotExistingBudgetException e) {
			e.printStackTrace();
			throw new NotExistingBudgetException("Budget doesn't exists");
		}

		if (!userId.equals(budget.getUser().getId())) {
			throw new ForbiddenException("You are not allowed to delete this budget!");
		}

		this.budgetDao.deleteBudgetById(budgetId);
	}
	
	public BudgetOverviewDTO getBudgetMovement(Long budgetId,Long userId) throws NotExistingBudgetException, ForbiddenException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		Budget budget = null;
		try {
			 budget = this.budgetDao.getBudgetById(budgetId);
		} catch (NotExistingBudgetException e) {
			e.printStackTrace();
			throw new NotExistingBudgetException("Budget doesn't exists");
		}
		
		if (!userId.equals(budget.getUser().getId())) {
			throw new ForbiddenException("You are not allowed to delete this budget!");
		}
		User user = this.usersRepo.findById(userId).get();
		
		Date startDate = this.getBudgetById(userId, budgetId).getStartDate();
		Date endDate = this.getBudgetById(userId, budgetId).getEndDate();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String sDate = df.format(startDate);
				String eDate = df.format(endDate);
				System.out.println(sDate);
	List<TransactionDTO> dtos = this.transactionService.
			getAllTransactionsOfUserForGivenCategory(user, null, null, null, null, sDate, eDate, budget.getCategory().getId());
	
	BudgetOverviewDTO budgetOverview = new BudgetOverviewDTO();
	budgetOverview.setBudgetId(budgetId);
	budgetOverview.setBudgetAmount(budget.getAmount());
	budgetOverview.setCategoryName(budget.getCategory().getName());
	budgetOverview.setPeriodOfBudget(sDate + "-" + eDate);
	budgetOverview.setTransactionCount(dtos.size());
	
	Double sum = dtos.stream().map(dto -> dto.getAmount()).reduce((double)0, 
            (d1, d2) -> d1+d2);
	budgetOverview.setMoneySpent(sum);
	budgetOverview.setFinalCalculation(budget.getAmount() - sum);
		return budgetOverview;
				
	}

}
