package com.financeManager.demo.services;

import java.sql.Date;
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
import com.financeManager.demo.dto.CrudBudgetDTO;
import com.financeManager.demo.exceptions.InvalidBudgetEntryException;
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

	@Autowired
	private IBudgetDAO budgetDao;
	@Autowired
	private IUsersRepository usersRepo;
	@Autowired
	private ICategoryDao categoryDao;
	@Autowired
	private IRepeatPeriodDAO repeatPeriodsDao;
	
	public List<BudgetDTO> getAllUserWallets(Long userId) {
		List<Budget> budgets = this.budgetDao.getAllUserBudgets(userId);

		if (budgets == null) {
			return new LinkedList<BudgetDTO>();
		}

		return budgets.stream().map(budget -> new BudgetDTO(budget.getAmount(), budget.getStartDate(), 
				budget.getEndDate(),
				this.categoryDao.getById(budget.getCategory().getId()).getName(), 
				this.repeatPeriodsDao.getById(budget.getRepeatPeriod().getId()).getPeriod()))
				.collect(Collectors.toList());

	}

	public void addBudgetToUser(CrudBudgetDTO newBudget, Long userId) throws InvalidBudgetEntryException {
		User owner = this.usersRepo.findById(userId).get();
		
		if (newBudget.getAmount() == null) {
			throw new InvalidBudgetEntryException("Invalid budget amount!");
		}
		
		if(newBudget.getCategory() == null) {
			throw new InvalidBudgetEntryException("You must choose category");
		}
		
		try {
			
			Category category = this.categoryDao.getById(newBudget.getCategory());
			RepeatPeriod repeatPeriod = this.repeatPeriodsDao.getById(newBudget.getRepeatPeriod());
			
			Date startDate = this.repeatPeriodsDao.calculateStartDateByPeriod(repeatPeriod);
			Date endDate = this.repeatPeriodsDao.calculateEndDateByPeriod(repeatPeriod);
			
			Budget budget = new Budget(newBudget.getAmount(), startDate, endDate, owner, category, repeatPeriod);
			
		}catch(NoSuchElementException e) {
			throw new InvalidBudgetEntryException("Bad input!");
		}
		
	}
	
	

}
