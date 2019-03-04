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
			throw new NotExistingBudgetException();
		}
	}
	
	@Override
	public void addBudget(Budget budget) {
		this.budgetRepo.save(budget);
		this.budgets.add(budget);
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
	public boolean deleteBudgetById(Long budgetId) {
		try {
			Budget b = this.budgets.stream().
					filter(budget -> budget.getId().equals(budgetId)).findFirst().get();
			System.out.println(b);
			this.budgets.remove(b);
			System.out.println(b);
			this.budgetRepo.delete(b);
			System.out.println(b);

			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	@Override
	public void saveUpdatedBudget(Long budgetId) throws NotExistingBudgetException {
		this.budgetRepo.save(this.getBudgetById(budgetId));
	}

	
}
