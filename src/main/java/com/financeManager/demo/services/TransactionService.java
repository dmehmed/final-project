package com.financeManager.demo.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.controllers.Helper;
import com.financeManager.demo.dao.ICategoryDao;
import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CreateTransactionDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
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
	
	public List<TransactionDTO> getAllTransactionsOfUserInWallet(User user, Long walletId, String sortBy, String orderBy) throws NotExistingWalletException {
		
		Optional<Wallet> result = this.walletDAO.getAllUserWallets(user.getId()).stream().filter(w -> w.getId().equals(walletId)).findFirst();

		if (!result.isPresent()) {
			throw new NotExistingWalletException();
		}
		
		Wallet wallet = result.get();
		
		List<Transaction> walletTransactions = this.transactionRepo.findAllByWalletId(wallet.getId());

		return walletTransactions.stream()
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction)).sorted(Helper.giveComparatorByCriteria(sortBy, orderBy))
				.collect(Collectors.toList());
	}

	public TransactionDTO getTransactionById(Long transactionId, Long userId)
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

	public List<TransactionDTO> getAllIncomeTransactions() {

		List<Transaction> incomes = this.transactionRepo.findAllByAmountIsGreaterThan(new Double(0));

		return incomes.stream().filter(transaction -> transaction.getAmount().doubleValue() > 0)
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.collect(Collectors.toList());
	}

	public List<TransactionDTO> getAllExpenseTransactions() {

		List<Transaction> expenses = this.transactionRepo.findAllByAmountIsLessThan(new Double(0));

		return expenses.stream().filter(transaction -> transaction.getAmount().doubleValue() < 0)
				.map(transaction -> this.convertFromTransactionToTransactionDTO(transaction))
				.collect(Collectors.toList());
	}

	private TransactionDTO convertFromTransactionToTransactionDTO(Transaction transaction) {
		TransactionDTO newTransactionDTO = new TransactionDTO();
		if (transaction.getAmount() < 0) {
			newTransactionDTO.setAmount(transaction.getAmount() * -1);
		}
		newTransactionDTO.setAmount(transaction.getAmount());
		newTransactionDTO.setCategoryType(transaction.getCategory().getName());
		newTransactionDTO.setWalletName(transaction.getWallet().getName());
		newTransactionDTO.setTransactionType(transaction.getCategory().getTransactionType().getName());
		newTransactionDTO.setCreationDate(transaction.getCreationDate());


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

	
	

		
	
}
