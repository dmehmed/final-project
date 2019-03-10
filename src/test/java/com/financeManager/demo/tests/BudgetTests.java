package com.financeManager.demo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.dao.IBudgetDAO;
import com.financeManager.demo.dto.BudgetDTO;
import com.financeManager.demo.dto.CrudBudgetDTO;
import com.financeManager.demo.exceptions.AlreadyExistingBudget;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidBudgetEntryException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.model.Budget;
import com.financeManager.demo.model.User;
import com.financeManager.demo.repositories.IBudgetRepository;
import com.financeManager.demo.services.BudgetService;
import com.financeManager.demo.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BudgetTests {

	@Autowired
	private BudgetService budgetService;
	@Autowired
	private IBudgetRepository budgetRepo;
	@Autowired
	private IBudgetDAO budgetDao;
	@Autowired
	private UserService userService;

	@Test
	public void testCreateUserBudget() throws NotExistingUserException, InvalidBudgetEntryException, AlreadyExistingBudget, NotExistingBudgetException {

		User user = this.userService.getExistingUserById(3L);
		budgetDao.loadUserBudgets(user.getId());

		CrudBudgetDTO newBudget = new CrudBudgetDTO(570.0, 5L, 2L);
		
		int countInRepoBefore = budgetRepo.findAllByUserId(user.getId()).size();
		int countInDaoBefore = budgetDao.getAllUserBudgets(user.getId()).size();

		Long newBudgetId = budgetService.addBudgetToUser(newBudget, user.getId());

		int countInRepoAfter = budgetRepo.findAllByUserId(user.getId()).size();
		int countInDaoAfter = budgetDao.getAllUserBudgets(user.getId()).size();
		
		assertTrue(countInRepoBefore + 1 == countInRepoAfter);
		assertTrue(countInDaoBefore + 1 == countInDaoAfter);
		assertNotNull(budgetRepo.findById(newBudgetId).get());
		assertNotNull(budgetDao.getBudgetById(newBudgetId));
		
		this.budgetDao.clearUserBudgets(user.getId());
	}
	
	@Test
	public void testGetAllUserBudgets() throws NotExistingUserException {
		
		User user = this.userService.getExistingUserById(3L);
		this.budgetDao.loadUserBudgets(user.getId());
		
		int budgetCountInDao = this.budgetService.getAllUserBugdets(user.getId()).size();
		int budgetCountInRepo = this.budgetRepo.findAllByUserId(user.getId()).stream().filter(budget -> budget.getIsDeleted() == 0).collect(Collectors.toList()).size();		
		
		assertTrue(budgetCountInDao == budgetCountInRepo);
		
		this.budgetDao.clearUserBudgets(user.getId());
	}

	@Test
	public void testUpdateUserBudget() throws NotExistingUserException, NotExistingBudgetException, InvalidBudgetEntryException, ForbiddenException {
		User user = this.userService.getExistingUserById(3L);
		this.budgetDao.loadUserBudgets(user.getId());
		
		CrudBudgetDTO updateBudget = new CrudBudgetDTO();
		updateBudget.setAmount(700.0);
		updateBudget.setRepeatPeriodId(2L);
		
		Long budgetId = 88L;
		
		this.budgetService.updateBudget(updateBudget, user.getId(), budgetId);
		
		Budget budgetInRepo = this.budgetRepo.findById(budgetId).get();
		Budget budgetInDao = this.budgetDao.getBudgetById(budgetId);
		
		assertEquals(budgetInRepo.getAmount(), updateBudget.getAmount());
		assertEquals(budgetInDao.getAmount(), updateBudget.getAmount());
		
		this.budgetDao.clearUserBudgets(user.getId());
	}
	
	@Test
	public void testViewUserBudget() throws NotExistingUserException, NotExistingBudgetException, ForbiddenException {
		User user = this.userService.getExistingUserById(3L);
		this.budgetDao.loadUserBudgets(user.getId());
		
		Long budgetId = 88L;
		
		BudgetDTO budget = this.budgetService.getBudgetById(user.getId(), budgetId);
		
		assertEquals(budget.getId(), budgetId);
		
		this.budgetDao.clearUserBudgets(user.getId());
	}
	
	@Test
	public void testDeleteUserBudget() throws NotExistingUserException, NotExistingBudgetException, ForbiddenException {
		User user = this.userService.getExistingUserById(3L);
		this.budgetDao.loadUserBudgets(user.getId());
		
		Long budgetId = 79L;
		
		this.budgetService.deleteBudgetById(user.getId(), budgetId);
		
		Budget budgetInRepo = this.budgetRepo.findById(budgetId).get();
		Budget budgetInDao = this.budgetDao.getAllUserBudgets(user.getId()).stream().filter(budget -> budget.getId().equals(budgetId)).findFirst().get();
		
		assertTrue(budgetInRepo.getIsDeleted() == 1);
		assertTrue(budgetInDao.getIsDeleted() == 1);
	}

}
