package com.financeManager.demo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.controllers.Helper;
import com.financeManager.demo.dao.ICategoryDao;
import com.financeManager.demo.dao.ITransactionTypeDAO;
import com.financeManager.demo.dao.IWalletDAO;
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
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.model.Category;
import com.financeManager.demo.model.Transaction;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.ITransactionRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionService {

//	private static final Long INCOME = 1L;
	private static final Long EXPENSE = 2L;

	@Autowired
	private ITransactionRepository transactionRepo;

	@Autowired
	private ICategoryDao categoryDAO;

	@Autowired
	private IWalletDAO walletDAO;

	@Autowired
	private ITransactionTypeDAO typeDAO;

	public List<TransactionDTO> getAllTransactionsOfUserInWallet(Long walletId, User user, String criteria,
			String orderBy, Double min, Double max, String startDate, String endDate)
			throws NotExistingWalletException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {

		Optional<Wallet> result = this.walletDAO.getAllUserWallets(user.getId()).stream()
				.filter(w -> w.getId().equals(walletId)).findFirst();

		if (!result.isPresent()) {
			throw new NotExistingWalletException("Not existing wallet!");
		}

		Wallet wallet = result.get();

		List<Transaction> walletTransactions = this.transactionRepo.findAllByWalletId(wallet.getId());

		List<TransactionDTO> dtos = walletTransactions.stream()
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());

		dtos = this.filterTransactionBetweenAmounts(min, max, dtos);
		dtos = this.filterTransactionByDate(dtos, startDate, endDate);

		return dtos;
	}

	private List<TransactionDTO> filterTransactionBetweenAmounts(Double min, Double max,
			List<TransactionDTO> transactions) throws InvalidAmountsEntryException {

		if (max == null && min == null) {
			return transactions;
		}

		if ((max == null || max == 0) && min != null && min > 0) {
			return transactions.stream().filter(transaction -> transaction.getAmount() >= min)
					.collect(Collectors.toList());
		}

		if ((min == null || min == 0) && max != null && max > 0) {
			return transactions.stream().filter(transaction -> transaction.getAmount() <= max)
					.collect(Collectors.toList());
		}

		if (min > max) {
			throw new InvalidAmountsEntryException("Bad amounts entered!");
		}

		if (max == min) {
			return transactions.stream().filter(transaction -> transaction.getAmount() == max)
					.collect(Collectors.toList());
		}

		if (max > min) {
			return transactions.stream()
					.filter(transaction -> transaction.getAmount() <= max && transaction.getAmount() >= min)
					.collect(Collectors.toList());
		}

		return null;
	}

	public TransactionDTO getTransactionById(Long transactionId, Long userId)
			throws NotExistingTransactionException, NotExistingWalletException, ForbiddenException {

		Optional<Transaction> result = transactionRepo.findById(transactionId);

		if (!result.isPresent()) {
			throw new NotExistingTransactionException("Transaction not existing!");
		}

		Transaction transaction = result.get();
		Wallet wallet = this.walletDAO.getWalletById(transaction.getWallet().getId());

		if (!wallet.getUser().getId().equals(userId)) {
			throw new ForbiddenException("Can't touch this!");
		}

		return this.convertFromTransactionToTransactionDTO(transaction);

	}

	public void deleteTransactionById(Long transactionId, Long userId)
			throws NotExistingTransactionException, NotExistingWalletException, UnauthorizedException {

		Optional<Transaction> result = transactionRepo.findById(transactionId);

		if (!result.isPresent()) {
			throw new NotExistingTransactionException();
		}

		Transaction transaction = result.get();

		Wallet wallet = this.walletDAO.getWalletById(transaction.getWallet().getId());

		if (!wallet.getUser().getId().equals(userId)) {
			throw new UnauthorizedException();
		}

		wallet.setBalance(wallet.getBalance() - transaction.getAmount());
		this.walletDAO.saveUpdatedWallet(wallet.getId());

		this.transactionRepo.deleteById(transactionId);

	}

	public Long createTransaction(CreateTransactionDTO newTransaction, Long userId)
			throws InvalidTransactionEntryException, NotExistingWalletException, InsufficientBalanceException,
			ExceededLimitException {

		Wallet userWallet;
		Category transactionCategory;

		try {
			userWallet = walletDAO.getWalletById(newTransaction.getWalletId());
		} catch (NotExistingWalletException e) {
			e.printStackTrace();
			throw new NotExistingWalletException("Invalid wallet", e);
		}

		try {
			transactionCategory = categoryDAO.getById(newTransaction.getCategoryId());
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new InvalidTransactionEntryException("Invalid category type");
		}

		Double amount = newTransaction.getAmount();

		if (transactionCategory.getTransactionType().getId().equals(EXPENSE)) {
			amount = -amount;
		}

		if (userWallet.getBalance() + amount < 0) {
			throw new InsufficientBalanceException("Insufficient account balance.");
		}

		if (userWallet.getBalance() + amount > userWallet.getLimit()) {
			throw new ExceededLimitException("Limit exceeded!");
		}

		Transaction transaction = new Transaction(amount, newTransaction.getDescription(), userWallet,
				transactionCategory);

		userWallet.setBalance(userWallet.getBalance() + amount);
		walletDAO.saveUpdatedWallet(userWallet.getId());

		this.transactionRepo.save(transaction);
		return transaction.getId();
	}

	public List<TransactionDTO> getAllIncomeTransactions(User us, String criteria, String orderBy) {

		List<Transaction> incomes = this.transactionRepo.findAllTransactionsByUser(us);

		return incomes.stream().filter(transaction -> transaction.getAmount().doubleValue() > 0)
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> getAllExpenseTransactions(User us, String criteria, String orderBy) {

		List<Transaction> expenses = this.transactionRepo.findAllTransactionsByUser(us);

		return expenses.stream().filter(transaction -> transaction.getAmount().doubleValue() < 0)
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}


	public TransactionDTO convertFromTransactionToTransactionDTO(Transaction transaction) {

		TransactionDTO newTransactionDTO = new TransactionDTO();

		if (transaction.getAmount() < 0) {
			newTransactionDTO.setAmount(transaction.getAmount() * -1);
		} else {
			newTransactionDTO.setAmount(transaction.getAmount());
		}

		newTransactionDTO.setId(transaction.getId());
		newTransactionDTO.setCategoryType(transaction.getCategory().getName());
		newTransactionDTO.setWalletName(transaction.getWallet().getName());
		newTransactionDTO.setTransactionType(transaction.getCategory().getTransactionType().getName());
		newTransactionDTO.setCreationDate(transaction.getCreationDate().toLocalDateTime());

		return newTransactionDTO;
	}

	public List<TransactionDTO> getAllTransactionsOfUser(User user, String criteria, String orderBy, Double min,
			Double max, String startDate, String endDate)
			throws InvalidAmountsEntryException, InvalidDateException, DateFormatException {

		List<Transaction> transactions = this.transactionRepo.findAllTransactionsByUser(user);

		List<TransactionDTO> dtos = transactions.stream()
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());

		dtos = this.filterTransactionBetweenAmounts(min, max, dtos);
		dtos = this.filterTransactionByDate(dtos, startDate, endDate);

		return dtos;
	}

	public List<TransactionDTO> getAllTransactionsOfUserForGivenCategory(User user, String criteria, String orderBy,
			Double min, Double max, String startDate, String endDate, Long categoryId)
			throws InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		List<Transaction> transactions = this.transactionRepo.findAllTransactionsByUser(user);

		List<TransactionDTO> dtos = transactions.stream()
				.filter(transaction -> transaction.getCategory().getId().equals(categoryId))
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());

		dtos = this.filterTransactionBetweenAmounts(min, max, dtos);
		dtos = this.filterTransactionByDate(dtos, startDate, endDate);

		return dtos;
	}

	public List<CategoryDTO> listAllCategories() {
		return this.categoryDAO.getAll().stream().map(category -> new CategoryDTO(category.getId(), category.getName()))
				.collect(Collectors.toList());
	}

	public List<TransactionTypeDTO> listAllTransactionTypes() {
		return this.typeDAO.getAll().stream().map(type -> new TransactionTypeDTO(type.getId(), type.getName()))
				.collect(Collectors.toList());
	}

	public List<TransactionDTO> filterTransactionByDate(List<TransactionDTO> transactions, String startDateString,
			String endDateString) throws InvalidDateException, DateFormatException {

		LocalDateTime startDate = Helper.parseStringToLocalDateTime(startDateString);
		LocalDateTime endDate = Helper.parseStringToLocalDateTime(endDateString);

		if (startDate == null && endDate == null) {
			return transactions;
		}

		if (startDate == null && endDate != null) {
			return transactions.stream().filter(transaction -> transaction.getCreationDate().isBefore(endDate))
					.collect(Collectors.toList());
		}

		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new InvalidDateException("Invalid data input!");
		}

		if (startDate != null && endDate == null) {
			return transactions.stream().filter(transaction -> transaction.getCreationDate().isAfter(startDate))
					.collect(Collectors.toList());
		}

		if (startDate == endDate) {
			return transactions.stream()
					.filter(transaction -> (transaction.getCreationDate().getDayOfYear() == endDate.getDayOfYear())
							&& transaction.getCreationDate().getYear() == endDate.getYear())
					.collect(Collectors.toList());
		}

		if (startDate != null && endDate != null) {
			return transactions.stream().filter(transaction -> transaction.getCreationDate().isAfter(startDate)
					&& transaction.getCreationDate().isBefore(endDate)).collect(Collectors.toList());
		}
		return null;
	}
	
	

}
