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
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.dto.TransactionTypeDTO;
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
import com.financeManager.demo.exceptions.ValidationException;
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
			HttpServletResponse response) throws UnauthorizedException, NotExistingTransactionException,
			NotExistingWalletException, ForbiddenException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		return this.transactionService.getTransactionById(id, (Long) session.getAttribute(Helper.USER_ID));

	}

	@GetMapping(path = "/wallet/{walletId}")
	public List<TransactionDTO> getAllTransactionsInWallet(@PathVariable Long walletId,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@RequestParam(name = "min", required = false) Double min,
			@RequestParam(name = "max", required = false) Double max,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, NotExistingWalletException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllTransactionsOfUserInWallet(walletId,user, sortBy, orderBy, min, max, startDate, endDate);

	}

	@DeleteMapping(path = "/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteWalletById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws NotExistingTransactionException, NotExistingWalletException, UnauthorizedException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);

		this.transactionService.deleteTransactionById(id, userId);

	}

	@PostMapping("/create")
	public String createTransaction(@RequestBody @Valid CreateTransactionDTO newTransaction, Errors errors,
			HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException, ValidationException,
			InvalidTransactionEntryException, NotExistingWalletException, InsufficientBalanceException, ExceededLimitException {

		Helper.isThereRequestError(errors, response);

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);

		this.transactionService.createTransaction(newTransaction, userId);

		response.setStatus(HttpStatus.CREATED.value());
		return HttpStatus.CREATED.getReasonPhrase();
	}

	@GetMapping("/incomes")
	public List<TransactionDTO> findAllIncomes(@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);
		response.setStatus(HttpStatus.NOT_FOUND.value());

		return this.transactionService.getAllIncomeTransactions(user, sortBy, orderBy);
	}

	@GetMapping("/expenses")
	public List<TransactionDTO> findAllExpenses(@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllExpenseTransactions(user, sortBy, orderBy);

	}

	@GetMapping()
	public List<TransactionDTO> getAllTransactionsOfUser(
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@RequestParam(name = "min", required = false) Double min,
			@RequestParam(name = "max", required = false) Double max,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, InvalidAmountsEntryException, InvalidDateException,
			DateFormatException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return transactionService.getAllTransactionsOfUser(user, sortBy, orderBy, min, max, startDate, endDate);
	}

	@GetMapping("/category/{id}")
	public List<TransactionDTO> giveTransactionsByCategory(@PathVariable Long id,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@RequestParam(name = "min", required = false) Double min,
			@RequestParam(name = "max", required = false) Double max,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllTransactionsOfUserForGivenCategory(user, sortBy, orderBy, min, max, startDate, endDate,id);

	}

	@GetMapping("/categories")
	public List<CategoryDTO> listAllCategories(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		return this.transactionService.listAllCategories();

	}

	@GetMapping("/types")
	public List<TransactionTypeDTO> listAllTransactionTypes(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		return this.transactionService.listAllTransactionTypes();
	}

}
