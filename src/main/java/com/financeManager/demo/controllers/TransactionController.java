package com.financeManager.demo.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.CategoryDTO;
import com.financeManager.demo.dto.CreateTransactionDTO;
import com.financeManager.demo.dto.ResponseDTO;
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

	/**
	 * Find transaction by id.
	 * @param id - id of the transaction.
	 * @param request
	 * @param response
	 * @return
	 * @throws UnauthorizedException
	 * @throws NotExistingTransactionException
	 * @throws NotExistingWalletException
	 * @throws ForbiddenException
	 */
	
	@GetMapping(path = "/{id}")
	public TransactionDTO getTransactionById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingTransactionException,
			NotExistingWalletException, ForbiddenException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		return this.transactionService.getTransactionById(id, (Long) session.getAttribute(Helper.USER_ID));

	}
	/**
	 * Find and show wallets transaction by ID of the wallet.
	 * @param walletId - ID of the desired wallet.
	 * @param sortBy - type of sorting.
	 * @param orderBy - ordering ascending or descenging
	 * @param min - min amount of transaction
	 * @param max -max amount of transaction
	 * @param startDate - start date
	 * @param endDate - end date.
	 * @param request
	 * @param response
	 * @return List of transactionDTO containing information about transaction.
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws NotExistingWalletException
	 * @throws InvalidAmountsEntryException
	 * @throws InvalidDateException
	 * @throws DateFormatException
	 */
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

		Long userId =	Helper.getLoggedUserId(request);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllTransactionsOfUserInWallet(walletId,user, sortBy, orderBy, min, max, startDate, endDate);

	}
	/**
	 * Delete a transaction by given ID.
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws NotExistingTransactionException
	 * @throws NotExistingWalletException
	 * @throws UnauthorizedException
	 */

	@DeleteMapping(path = "/delete/{id}")
	public ResponseEntity<ResponseDTO> deleteTransactionById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws NotExistingTransactionException, NotExistingWalletException, UnauthorizedException {
		Long userId =	Helper.getLoggedUserId(request);
		this.transactionService.deleteTransactionById(id, userId);
		return Helper.createResponse(id, "Transaction deleted", HttpStatus.NO_CONTENT);
	}
	/**
	 * Create a transaction for the user.
	 * @param newTransaction - CreateTransactionDTO containing amount, category and wallet of the transaction.
	 * @param errors
	 * @param request
	 * @param response
	 * @return Response Entity containing info about the new created transaction.
	 * @throws UnauthorizedException
	 * @throws ValidationException
	 * @throws InvalidTransactionEntryException
	 * @throws NotExistingWalletException
	 * @throws InsufficientBalanceException
	 * @throws ExceededLimitException
	 */
	@PostMapping("/create")
	public ResponseEntity<ResponseDTO> createTransaction(@RequestBody @Valid CreateTransactionDTO newTransaction, Errors errors,
			HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException, ValidationException,
			InvalidTransactionEntryException, NotExistingWalletException, InsufficientBalanceException, ExceededLimitException {

		Helper.isThereRequestError(errors, response);

		Long userId =	Helper.getLoggedUserId(request);

		Long transactionId = this.transactionService.createTransaction(newTransaction, userId);

		return Helper.createResponse(transactionId, "You successfully made a transaction!", HttpStatus.CREATED);
	}

	
	/**
	 * Find all incomes of the logged user.
	 * @param sortBy - type of sort.
	 * @param orderBy - order ascending or descending
	 * @param request
	 * @param response
	 * @return List of TransactionDTO for all income transaction containing some info about transactions.
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 */
	@GetMapping("/incomes")
	public List<TransactionDTO> findAllIncomes(@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException {
		Long userId =	Helper.getLoggedUserId(request);
		User user = this.userService.getExistingUserById(userId);
		response.setStatus(HttpStatus.NOT_FOUND.value());

		return this.transactionService.getAllIncomeTransactions(user, sortBy, orderBy);
	}
	 /**
	  * Find all expenses of the logged user.	
	  * @param sortBy - type of sort.
	  * @param orderBy - order ascending or descending
	  * @param request
	  * @param response
	  * @return List of TransactionDTO for all expense transaction containing some info about transactions.
	  * @throws UnauthorizedException
	  * @throws NotExistingUserException
	  */
	@GetMapping("/expenses")
	public List<TransactionDTO> findAllExpenses(@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException {

		Long userId =	Helper.getLoggedUserId(request);
		User user = this.userService.getExistingUserById(userId);

		return this.transactionService.getAllExpenseTransactions(user, sortBy, orderBy);

	}
	/**
	 * Find all transaction of users filtered or sorted.
	 * @param sortBy - type of sort. 
	 * @param orderBy - type of order - ascending or descending
	 * @param min - min value of transaction
	 * @param max - max value of transaction
	 * @param startDate - start date
	 * @param endDate - end date.
	 * @param request
	 * @param response
	 * @return List of TransactionDTO for all transaction after filters containing some info about transactions.
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws InvalidAmountsEntryException
	 * @throws InvalidDateException
	 * @throws DateFormatException
	 */
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

		Long userId =	Helper.getLoggedUserId(request);
		User user = this.userService.getExistingUserById(userId);

		return transactionService.getAllTransactionsOfUser(user, sortBy, orderBy, min, max, startDate, endDate);
	}
	
	/**
	 * Find all transactions for a given category by the ID of the category
	 * @param id - id of the category
	 * @param sortBy - type of sort. 
	 * @param orderBy - type of order - ascending or descending
	 * @param min - min value of transaction
	 * @param max - max value of transaction
	 * @param startDate - start date
	 * @param endDate - end date.
	 * @param request
	 * @param response
	 * @return List of TransactionDTO for all transactions for the category after filters containing some info about transactions.
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws InvalidAmountsEntryException
	 * @throws InvalidDateException
	 * @throws DateFormatException
	 */
	
	@GetMapping("/category/{id}")
	public List<TransactionDTO> giveTransactionsByCategory(@PathVariable Long id,
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@RequestParam(name = "min", required = false) Double min,
			@RequestParam(name = "max", required = false) Double max,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {

		Long userId =	Helper.getLoggedUserId(request);
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
