package com.financeManager.demo.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.CreateTransactionDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidTransactionEntryException;
import com.financeManager.demo.exceptions.NotExistingTransactionException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.model.Transaction;
import com.financeManager.demo.model.User;
import com.financeManager.demo.repositories.ITransactionRepository;
import com.financeManager.demo.repositories.IUsersRepository;
import com.financeManager.demo.services.TransactionService;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	
	@DeleteMapping(path = "/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteWalletById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		

		if (!Helper.isThereLoggedUser(response, session)) {
			return;
		}
		
		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		
		try {
			this.transactionService.deleteTransactionById(id, userId);
		} catch (NotExistingTransactionException | NotExistingWalletException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
	}
	
	@PostMapping("/create")
	public String createTransaction(@RequestBody @Valid CreateTransactionDTO newTransaction, Errors errors, 
			HttpServletRequest request, HttpServletResponse response) {
		
		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			return HttpStatus.UNAUTHORIZED.getReasonPhrase();
		}
		
		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		
		try {
			this.transactionService.createTransaction(newTransaction, userId);
		} catch (NotExistingWalletException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return HttpStatus.FORBIDDEN.getReasonPhrase();
		} catch (InvalidTransactionEntryException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return e.getMessage();
		} catch (InsufficientBalanceException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return e.getMessage();
		} 
		
		response.setStatus(HttpStatus.CREATED.value());
		return HttpStatus.CREATED.getReasonPhrase();
	}
	
	
	@GetMapping("/incomes")
	public List<TransactionDTO> findAllIncomes(){
		return this.transactionService.getAllIncomeTransactions();
	}
	
	
	@GetMapping("/expenses")
	public List<TransactionDTO> findAllExpenses(){
		return this.transactionService.getAllExpenseTransactions();
	}
	
	
	@GetMapping("/user/{id}")
	public List<TransactionDTO> giveTransaction(@PathVariable Long id) {
//		User user = this.userRepo.findById(id).get();
	return null;
	}
	
//	@GetMapping("/all")
//	public List<TransactionDTO> findAllTransactionsOfUser(@PathVariable Long id){
//		return this.transactionService.getAllTransactionsOfUser(id);
//	}
//	
	
}
