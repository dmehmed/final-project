package com.financeManager.demo.services;

import java.sql.Timestamp;
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

	public List<TransactionDTO> getAllTransactionsOfUserInWallet(User user, Long walletId, String sortBy,
			String orderBy) throws NotExistingWalletException {

		Optional<Wallet> result = this.walletDAO.getAllUserWallets(user.getId()).stream()
				.filter(w -> w.getId().equals(walletId)).findFirst();

		if (!result.isPresent()) {
			throw new NotExistingWalletException("Not existing wallet!");
		}

		Wallet wallet = result.get();

		List<Transaction> walletTransactions = this.transactionRepo.findAllByWalletId(wallet.getId());

		return walletTransactions.stream().map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(sortBy, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> giveAllTransactionInWalletBetweenAmounts(User user,
			TransactionBetweenAmountsDTO amounts, Long walletId, String sortBy, String orderBy)
			throws NotExistingWalletException, InvalidAmountsEntryException {

		List<TransactionDTO> walletTransactions = this.getAllTransactionsOfUserInWallet(user, walletId, sortBy,
				orderBy);

		return filterTransactionBetweenAmounts(amounts, walletTransactions);

	}

	private List<TransactionDTO> filterTransactionBetweenAmounts(TransactionBetweenAmountsDTO amounts,
			List<TransactionDTO> transactions) throws InvalidAmountsEntryException {

		if (amounts.getMax() == null && amounts.getMin() == null) {
			return transactions;
		}

		if ((amounts.getMax() == null || amounts.getMax() == 0) && amounts.getMin() != null && amounts.getMin() > 0) {
			return transactions.stream().filter(transaction -> transaction.getAmount() >= amounts.getMin())
					.collect(Collectors.toList());
		}

		if ((amounts.getMin() == null || amounts.getMin() == 0) && amounts.getMax() != null && amounts.getMax() > 0) {
			return transactions.stream().filter(transaction -> transaction.getAmount() <= amounts.getMax())
					.collect(Collectors.toList());
		}

		if (amounts.getMin() > amounts.getMax()) {
			throw new InvalidAmountsEntryException("Bad amounts entered!");
		}

		if (amounts.getMax() == amounts.getMin()) {
			return transactions.stream().filter(transaction -> transaction.getAmount() == amounts.getMax())
					.collect(Collectors.toList());
		}

		if (amounts.getMax() > amounts.getMin()) {
			return transactions.stream().filter(transaction -> transaction.getAmount() <= amounts.getMax()
					&& transaction.getAmount() >= amounts.getMin()).collect(Collectors.toList());
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

	public void createTransaction(CreateTransactionDTO newTransaction, Long userId)
			throws InvalidTransactionEntryException, NotExistingWalletException, InsufficientBalanceException {

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

		Transaction transaction = new Transaction(amount, newTransaction.getDescription(), userWallet,
				transactionCategory);

		userWallet.setBalance(userWallet.getBalance() + amount);
		walletDAO.saveUpdatedWallet(userWallet.getId());

		this.transactionRepo.save(transaction);
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

	private TransactionDTO convertFromTransactionToTransactionDTO(Transaction transaction) {
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

	public List<TransactionDTO> getAllTransactionsOfUser(User user, String criteria, String orderBy) {

		List<Transaction> transactions = this.transactionRepo.findAllTransactionsByUser(user);

		return transactions.stream().map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> getAllTransactionsOfUserForGivenCategory(User user, String criteria, String orderBy,
			Long categoryId) {
		List<Transaction> transactions = this.transactionRepo.findAllTransactionsByUser(user);

		return transactions.stream().filter(transaction -> transaction.getCategory().getId().equals(categoryId))
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> getAllTransactionsOfUserForGivenCategoryBetweenAmounts(User user,
			TransactionBetweenAmountsDTO amounts, String criteria, String orderBy, Long categoryId)
			throws InvalidAmountsEntryException {

		List<TransactionDTO> allTransactionOfUserForCategory = this.getAllTransactionsOfUserForGivenCategory(user,
				criteria, orderBy, categoryId);

		return this.filterTransactionBetweenAmounts(amounts, allTransactionOfUserForCategory);
	}

	public List<CategoryDTO> listAllCategories() {
		return this.categoryDAO.getAll().stream().map(category -> new CategoryDTO(category.getId(), category.getName()))
				.collect(Collectors.toList());
	}

	public List<TransactionTypeDTO> listAllTransactionTypes() {
		return this.typeDAO.getAll().stream().map(type -> new TransactionTypeDTO(type.getId(), type.getName()))
				.collect(Collectors.toList());
	}

	public List<TransactionDTO> getAllTransactionsBetweenDates(User user, TransactionByDateDTO dates, String sortBy,
			String orderBy) throws InvalidDateException {

		Timestamp startDate = Helper.parseStringToTimeStamp(dates.getStartDate());
		Timestamp endDate = Helper.parseStringToTimeStamp(dates.getEndDate());

		if (startDate == null && endDate == null) {
			return this.getAllTransactionsOfUser(user, sortBy, orderBy);
		}

		List<Transaction> transactions = null;

		if (startDate == null && endDate != null) {
			transactions = this.transactionRepo.findAllTransactionsByUserAndCreationDateIsBefore(user, endDate);
		}

		if (startDate != null && startDate.after(endDate)) {
			throw new InvalidDateException("Invalid data input!");
		}

		if (startDate != null && endDate == null) {
			transactions = this.transactionRepo.findAllTransactionsByUserAndCreationDateIsAfter(user, startDate);
		}

		if (startDate == endDate) {
			transactions = this.transactionRepo.findAllTransactionsByUserAndCreationDateIsEquals(user, startDate);
		}

		if (startDate != null && endDate != null) {
			transactions = this.transactionRepo.findAllTransactionsByUserAndCreationDateIsBetween(user, startDate,
					endDate);
		}

		return transactions.stream().map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(sortBy, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> listAllTransactionsSmallerThan(User user, Double amount, String criteria,
			String orderBy) {
		List<Transaction> smallers = this.transactionRepo.findAllTransactionsByUserWhereAmountIsLessThan(user, amount);

		return smallers.stream().map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> listAllTransactionsGreaterThan(User user, Double amount, String criteria,
			String orderBy) {
		List<Transaction> greater = this.transactionRepo.findAllTransactionsByUserWhereAmountIsGreaterThan(user,
				amount);

		return greater.stream().map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> listAllTransactionsBetween(User user, Double min, Double max, String criteria,
			String orderBy) {
		List<Transaction> between = this.transactionRepo.findAllTransactionsByUserWhereAmountIsBetween(user, min, max);
		return between.stream().map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> listAllTransactionsEqualsTo(User user, Double value, String criteria, String orderBy) {
		List<Transaction> transactions = this.transactionRepo.findAllTransactionsByUserWhereAmountEquals(user, value);

		return transactions.stream().map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.sorted(Helper.giveComparatorByCriteria(criteria, orderBy)).collect(Collectors.toList());
	}

	public List<TransactionDTO> getTransactionsBetweenAmounts(User user, TransactionBetweenAmountsDTO amounts,
			String sortBy, String orderBy) throws InvalidAmountsEntryException {

		if (amounts.getMax() == null && amounts.getMin() == null) {
			return this.getAllTransactionsOfUser(user, sortBy, orderBy);
		}

		if (amounts.getMin() == null || amounts.getMin() == 0) {
			return this.listAllTransactionsSmallerThan(user, amounts.getMax(), sortBy, orderBy);
		}

		if (amounts.getMax() == null || amounts.getMax() == 0) {
			return this.listAllTransactionsGreaterThan(user, amounts.getMin(), sortBy, orderBy);
		}

		if (amounts.getMin() > amounts.getMax()) {
			throw new InvalidAmountsEntryException();
		}

		if (amounts.getMax() == amounts.getMin()) {
			return this.listAllTransactionsEqualsTo(user, amounts.getMin(), sortBy, orderBy);
		}

		return this.listAllTransactionsBetween(user, amounts.getMin(), amounts.getMax(), sortBy, orderBy);
	}

	public List<TransactionDTO> giveAllTransactionInWalletBetweenDates(User user, TransactionByDateDTO dates,
			Long walletId, String sortBy, String orderBy) throws NotExistingWalletException, InvalidDateException {

		List<TransactionDTO> walletTransactions = this.getAllTransactionsOfUserInWallet(user, walletId, sortBy,
				orderBy);

		LocalDateTime startDate = Helper.parseStringToLocalDateTime(dates.getStartDate());
		LocalDateTime endDate = Helper.parseStringToLocalDateTime(dates.getEndDate());

		return filterTransactionByDate(walletTransactions, startDate, endDate);
	}

	private List<TransactionDTO> filterTransactionByDate(List<TransactionDTO> walletTransactions,
			LocalDateTime startDate, LocalDateTime endDate) throws InvalidDateException {
		if (startDate == null && endDate == null) {
			return walletTransactions;
		}

		if (startDate == null && endDate != null) {
			return walletTransactions.stream().filter(transaction -> transaction.getCreationDate().isBefore(endDate))
					.collect(Collectors.toList());
		}

		if (startDate != null && startDate.isAfter(endDate)) {
			throw new InvalidDateException("Invalid data input!");
		}

		if (startDate != null && endDate == null) {
			return walletTransactions.stream().filter(transaction -> transaction.getCreationDate().isAfter(startDate))
					.collect(Collectors.toList());
		}

		if (startDate == endDate) {
			return walletTransactions.stream().filter(transaction -> transaction.getCreationDate().isEqual(endDate))
					.collect(Collectors.toList());
		}

		if (startDate != null && endDate != null) {
			return walletTransactions.stream().filter(transaction -> transaction.getCreationDate().isAfter(startDate)
					&& transaction.getCreationDate().isBefore(endDate)).collect(Collectors.toList());
		}

		return null;
	}

	public List<TransactionDTO> giveAllTransactionByCategoryBetweenDates(User user, TransactionByDateDTO dates,
			Long categoryId, String sortBy, String orderBy) throws InvalidDateException {

		List<TransactionDTO> walletTransactions = this.getAllTransactionsOfUserForGivenCategory(user, sortBy, orderBy,
				categoryId);

		LocalDateTime startDate = Helper.parseStringToLocalDateTime(dates.getStartDate());
		LocalDateTime endDate = Helper.parseStringToLocalDateTime(dates.getEndDate());

		return filterTransactionByDate(walletTransactions, startDate, endDate);

	}

}
