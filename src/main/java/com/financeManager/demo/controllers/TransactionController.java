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
import com.financeManager.demo.dto.TransactionByDateDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.dto.TransactionTypeDTO;
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

	@PostMapping(path = "/betweenDates")
	public List<TransactionDTO> listAllTransactionsBetweenDates(@RequestBody TransactionByDateDTO dates,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, InvalidDateException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;

		user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllTransactionsBetweenDates(user, dates, sortBy, orderBy);

	}

	@PostMapping(path = "/wallet/{walletId}/betweenDates")
	public List<TransactionDTO> getAllTransactionsInWalletBetweenDates(@RequestBody TransactionByDateDTO dates,
			@PathVariable Long walletId, @RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, NotExistingWalletException, InvalidDateException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;

		user = this.userService.getExistingUserById(userId);

		return this.transactionService.giveAllTransactionInWalletBetweenDates(user, dates, walletId, sortBy, orderBy);

	}

	@PostMapping(path = "/category/{categoryId}/betweenDates")
	public List<TransactionDTO> getAllTransactionsByCategoryBetweenDates(@RequestBody TransactionByDateDTO dates,
			@PathVariable Long categoryId, @RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, InvalidDateException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;

		user = this.userService.getExistingUserById(userId);

		return this.transactionService.giveAllTransactionByCategoryBetweenDates(user, dates, categoryId, sortBy,
				orderBy);

	}

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
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, NotExistingWalletException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = null;

		user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllTransactionsOfUserInWallet(user, walletId, sortBy, orderBy);

	}

	@GetMapping(path = "/wallet/{walletId}/betweenAmounts")
	public List<TransactionDTO> getAllTransactionInWalletBetweenAmounts(@PathVariable Long walletId,
			@RequestBody TransactionBetweenAmountsDTO amounts,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws NotExistingUserException, UnauthorizedException,
			NotExistingWalletException, InvalidAmountsEntryException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.giveAllTransactionInWalletBetweenAmounts(user, amounts, walletId, sortBy,
				orderBy);

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
			InvalidTransactionEntryException, NotExistingWalletException, InsufficientBalanceException {

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
	public List<TransactionDTO> giveTransactions(@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return transactionService.getAllTransactionsOfUser(user, sortBy, orderBy);
	}

	@GetMapping("/category/{id}")
	public List<TransactionDTO> giveTransactionsByCategory(@PathVariable Long id,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllTransactionsOfUserForGivenCategory(user, sortBy, orderBy, id);

	}

	@GetMapping("/category/{id}/betweenAmounts")
	public List<TransactionDTO> getAllTransactionInCategoryBetweenAmounts(@PathVariable Long id,
			@RequestBody @Valid TransactionBetweenAmountsDTO amounts,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, InvalidAmountsEntryException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllTransactionsOfUserForGivenCategoryBetweenAmounts(user, amounts, sortBy,
				orderBy, id);
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

	@GetMapping("/betweenAmounts")
	public List<TransactionDTO> listAllTransactionsSmallerThan(@RequestBody TransactionBetweenAmountsDTO amounts,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, InvalidAmountsEntryException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getTransactionsBetweenAmounts(user, amounts, sortBy, orderBy);

	}

}
