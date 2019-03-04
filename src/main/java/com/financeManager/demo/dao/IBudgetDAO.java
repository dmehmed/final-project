package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.model.Budget;

public interface IBudgetDAO {
	
	Budget getBudgetById(Long id) throws NotExistingBudgetException;
	void addBudget(Budget budget);
	List<Budget> getAllUserBudgets(Long userId);
	void loadUserBudgets(Long userId);
	void clearUserBudgets(Long userId);
	boolean deleteBudgetById(Long budgetId);
	void saveUpdatedBudget(Long budgetId) throws NotExistingBudgetException;
	
}
