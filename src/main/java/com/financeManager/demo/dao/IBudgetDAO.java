package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.exceptions.AlreadyExistingBudget;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.model.Budget;

public interface IBudgetDAO {
	
	Budget getBudgetById(Long id) throws NotExistingBudgetException;
	Long addBudget(Budget budget) throws AlreadyExistingBudget;
	List<Budget> getAllUserBudgets(Long userId);
	List<Budget> getActiveUserBudgets(Long userId);
	void loadUserBudgets(Long userId);
	void clearUserBudgets(Long userId);
	void deleteBudgetById(Long budgetId);
	void saveUpdatedBudget(Long budgetId) throws NotExistingBudgetException;
}
