package com.financeManager.demo.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.CategoryDTO;
import com.financeManager.demo.dto.CreateTransactionDTO;
import com.financeManager.demo.dto.TransactionBetweenAmountsDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.dto.TransactionTypeDTO;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidAmountsEntry;
import com.financeManager.demo.exceptions.InvalidTransactionEntryException;
import com.financeManager.demo.exceptions.NotExistingTransactionException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.services.TransactionService;
import com.financeManager.demo.services.UserService;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	@Autowired
	private UserService userService;
	

	@GetMapping(path = "/{id}")
	public TransactionDTO getTransactionById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			return null;
		}

		try {
			return this.transactionService.getTransactionById(id, (Long) session.getAttribute(Helper.USER_ID));
		} catch (NotExistingTransactionException | NotExistingWalletException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return null;
		}

	}
	
	@GetMapping(path = "/wallet/{walletId}")
	public List<TransactionDTO> getAllTransactionsInWallet(@PathVariable Long walletId, 
			@RequestParam(name = "sortBy", required = false) String sortBy, 
			@RequestParam(name = "orderBy", required = false) String orderBy,
			HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
		
		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;
		
		try {
			user = this.userService.getExistingUserById(userId);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		try {
			return this.transactionService.getAllTransactionsOfUserInWallet(user,walletId, sortBy, orderBy);
		} catch (NotExistingWalletException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		} 
	}
	
	@GetMapping(path = "/wallet/{walletId}/betweenAmounts")
	public List<TransactionDTO> getAllTransactionInWalletBetweenAmounts(
			@PathVariable Long walletId, 
			@RequestBody TransactionBetweenAmountsDTO amounts,
			@RequestParam(name = "sortBy", required = false) String sortBy, 
			@RequestParam(name = "orderBy", required = false) String orderBy,
			HttpServletRequest request, HttpServletResponse response){
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
		
		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;
		
		try {
			user = this.userService.getExistingUserById(userId);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		try {
			return this.transactionService.giveAllTransactionInWalletBetweenAmounts(user,amounts,walletId, sortBy, orderBy);
		} catch (NotExistingWalletException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		} catch (InvalidAmountsEntry e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return null;
		} 
	}
	

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
			response.setStatus(HttpStatus.FORBIDDEN.value());
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
	public List<TransactionDTO> findAllIncomes(@RequestParam(name="sortBy",required = false) String sortBy,
			@RequestParam(name="orderBy",required = false)String orderBy,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;
		try {
			user = this.userService.getExistingUserById(userId);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		return this.transactionService.getAllIncomeTransactions(user,sortBy,orderBy);
	}

	@GetMapping("/expenses")
	public List<TransactionDTO> findAllExpenses(@RequestParam(name="sortBy",required = false) String sortBy,
			@RequestParam(name="orderBy",required = false)String orderBy,
			HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;
		try {
			user = this.userService.getExistingUserById(userId);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		return this.transactionService.getAllExpenseTransactions(user,sortBy,orderBy);
	
	}

	@GetMapping()
	public List<TransactionDTO> giveTransactions(@RequestParam(name="sortBy",required = false) String sortBy,
			@RequestParam(name="orderBy",required = false)String orderBy,
			HttpServletRequest request, HttpServletResponse response) {



		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;
		try {
			user = this.userService.getExistingUserById(userId);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}


		return transactionService.getAllTransactionsOfUser(user, sortBy, orderBy);
	}
	
	@GetMapping("/category/{id}")
	public List<TransactionDTO> giveTransactionsByCategory(
			@PathVariable Long id,
			@RequestBody TransactionBetweenAmountsDTO amounts,
			@RequestParam(name="sortBy",required = false) String sortBy,
			@RequestParam(name="orderBy",required = false)String orderBy,
			HttpServletRequest request, HttpServletResponse response){
		
			HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;
		
		try {
			user = this.userService.getExistingUserById(userId);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}

		return this.transactionService.getAllTransactionsOfUserForGivenCategory(user, sortBy, orderBy, id);

	}
	

	@GetMapping("/categories")
	public List<CategoryDTO> listAllCategories(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}

		return this.transactionService.listAllCategories();
		
	}
	
	@GetMapping("/types")
	public List<TransactionTypeDTO> listAllTransactionTypes(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
		
		return this.transactionService.listAllTransactionTypes();
	}
	

	
	@GetMapping("/betweenAmounts")
	public List<TransactionDTO> listAllTransactionsSmallerThan(@RequestBody TransactionBetweenAmountsDTO amounts,
			@RequestParam(name="sortBy",required = false) String sortBy,
			@RequestParam(name="orderBy",required = false)String orderBy,
			HttpServletRequest request, HttpServletResponse response){
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;
		try {
			user = this.userService.getExistingUserById(userId);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		if(amounts.getMin() == null || amounts.getMin() == 0) {
			return this.transactionService.listAllTransactionsSmallerThan(user,amounts.getMax(),sortBy, orderBy);
		}
		
		if(amounts.getMax() == null || amounts.getMax() == 0) {
			return this.transactionService.listAllTransactionsGreaterThan(user, amounts.getMin(), sortBy, orderBy);
		}
		return this.transactionService.listAllTransactionsBetween(user, amounts.getMin(), amounts.getMax(), sortBy, orderBy);
	}

}
