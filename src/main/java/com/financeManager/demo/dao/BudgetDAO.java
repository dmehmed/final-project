package com.financeManager.demo.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.model.Budget;
import com.financeManager.demo.repositories.IBudgetRepository;

@Component
public class BudgetDAO implements IBudgetDAO {
	
	@Autowired
	private IBudgetRepository budgetRepo;
	private List<Budget> budgets = new LinkedList<Budget>();
	
	@Override
	public Budget getBudgetById(Long id) throws NotExistingBudgetException {
		try{
			return this.budgets.stream().filter(budget -> budget.getId().equals(id)).findAny().get();
		} catch (NoSuchElementException e) {
			throw new NotExistingBudgetException("This budget does not exist!");
		}
	}
	
	@Override
	public Long addBudget(Budget budget) {
		this.budgetRepo.save(budget);
		this.budgets.add(budget);
		return budget.getId();
	}
	
	@Override
	public List<Budget> getAllUserBudgets(Long userId) {
		return this.budgets.stream().filter(budget -> budget.getUser().getId().equals(userId))
				.collect(Collectors.toList());
	}
	
	@Override
	public void loadUserBudgets(Long userId) {
		this.budgets.addAll(this.budgetRepo.findAllByUserId(userId));			
	}
	
	
	@Override
	public void clearUserBudgets(Long userId) {
		this.budgets = this.budgets.stream().filter(budget -> !budget.getUser().getId().equals(userId))
				.collect(Collectors.toList());		
	}
	
	@Override
	public void deleteBudgetById(Long budgetId) {

			Budget b = this.budgets.stream().
					filter(budget -> budget.getId().equals(budgetId)).findFirst().get();
			this.budgets.remove(b);

			this.budgetRepo.deleteById(budgetId);
	}
	
	@Override
	public void saveUpdatedBudget(Long budgetId) throws NotExistingBudgetException {
		this.budgetRepo.save(this.getBudgetById(budgetId));
	}

	
}
