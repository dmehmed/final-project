package com.financeManager.demo.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.dao.WalletDAO;
import com.financeManager.demo.dto.CreateTransactionDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.ExceededLimitException;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidAmountsEntryException;
import com.financeManager.demo.exceptions.InvalidDateException;
import com.financeManager.demo.exceptions.InvalidTransactionEntryException;
import com.financeManager.demo.exceptions.NotExistingTransactionException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.IWalletRepository;
import com.financeManager.demo.services.TransactionService;
import com.financeManager.demo.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionsTests {
@Autowired
private TransactionService transactionService;
@Autowired
private UserService userService;
@Autowired
private WalletDAO walletDao;
@Autowired
private IWalletRepository walletRepo;


	
	@Test
	public void getAllTransactionsOfUserInWallet() throws NotExistingUserException, NotExistingWalletException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		User user = this.userService.getExistingUserById(new Long(9));
		walletDao.loadUserWallets(new Long(9));
		List<TransactionDTO> transactions = this.transactionService
		.getAllTransactionsOfUserInWallet(new Long(91), user, null, null, null, null,null, null);
		assertNotNull(transactions);
	}
	
	@Test
	public void getTransactionById() throws NotExistingTransactionException, NotExistingWalletException, ForbiddenException {
		Long userId = new Long(9);
		Long transactionId = new Long(102);
		TransactionDTO tr = this.transactionService.getTransactionById(transactionId, userId);
		assertNotNull(tr);	
	}
	@Test
	public void deleteTransactionById() throws NotExistingWalletException, UnauthorizedException, ForbiddenException, NotExistingTransactionException {
		Long userId = new Long(9);
		Long transactionId = new Long(102);
		 this.transactionService.deleteTransactionById(transactionId, userId);
		 TransactionDTO tr;
		 try {
		 tr = this.transactionService.getTransactionById(transactionId, userId);
		 } catch (NotExistingTransactionException e) {
		  tr = null;
		  
		 }
		 assertNull(tr);
	}

	@Test
	public void createTransaction() throws InvalidTransactionEntryException, NotExistingWalletException, InsufficientBalanceException, ExceededLimitException {
		CreateTransactionDTO trCreate = new CreateTransactionDTO();
		Wallet w = walletRepo.findById(new Long(101)).get();
		walletDao.addWallet(w);
		double walletBalance = w.getBalance();
		trCreate.setAmount(new Double(350));
		trCreate.setCategoryId(new Long(2));
		trCreate.setWalletId(new Long(101));
		Long id = this.transactionService.createTransaction(trCreate, w.getUser().getId());
		assertNotNull(id);
		double walletBalanceNew = w.getBalance();
		assertTrue(walletBalance != walletBalanceNew);

	}
	
	@Test
	public void getAllExpenses() throws NotExistingUserException {
		User us = this.userService.getExistingUserById(new Long(9));
		List<TransactionDTO> dtos = this.transactionService.getAllExpenseTransactions(us, null,null);	
		assertNotNull(dtos);
		
	}
	
	@Test
	public void getAllIncomes() throws NotExistingUserException {
		User us = this.userService.getExistingUserById(new Long(9));
		List<TransactionDTO> dtos = this.transactionService.getAllIncomeTransactions(us, null,null);	
		assertNotNull(dtos);
	}

	@Test
	public void getAllTransactionsOfUser() throws InvalidAmountsEntryException, InvalidDateException, DateFormatException, NotExistingUserException {
		User us = this.userService.getExistingUserById(new Long(9));
		List<TransactionDTO> dtos = 
				this.transactionService.getAllTransactionsOfUser(us, null,null,null,null,null,null);
		assertNotNull(dtos);
	}

	@Test 
	public void filterTransactionsByDate() throws NotExistingUserException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		User us = this.userService.getExistingUserById(new Long(9));
		List<TransactionDTO> dtos = 
				this.transactionService.getAllTransactionsOfUser(us, null,null,null,null,null,null);
		
		List<TransactionDTO> filteredDtos= 
				this.transactionService.filterTransactionByDate(dtos, "2019-03-07","2019-03-08");
		
		assertTrue(dtos.size() != filteredDtos.size());
	}



}
