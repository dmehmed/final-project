package com.financeManager.demo.services;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.ICategoryDao;
import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CreateTransactionDTO;
import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidTransactionEntryException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.Category;
import com.financeManager.demo.model.Transaction;
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
				
		if(transactionCategory.getTransactionType().getId().equals(EXPENSE)) {
			 amount = -amount;
		}
		
		if(userWallet.getBalance() + amount < 0) {
			throw new InsufficientBalanceException("Insufficient account balance.");
		}

		Transaction transaction = new Transaction(amount, newTransaction.getDescription(),
				userWallet, transactionCategory);
		
		userWallet.setBalance(userWallet.getBalance() + amount);
		walletDAO.saveUpdatedWallet(userWallet.getId());
		
		this.transactionRepo.save(transaction);
	}
	
	
	
	public List<TransactionDTO> getAllIncomeTransactions(){
		List<Transaction> incomes = this.transactionRepo.findAllByAmountIsGreaterThan(new Double(0));
		List<TransactionDTO> incomesDTO = new LinkedList<TransactionDTO>();
		for(Transaction tr : incomes) {
			TransactionDTO newTransactionDTO = new TransactionDTO();
			newTransactionDTO.setAmount(tr.getAmount());
			newTransactionDTO.setCategoryType(tr.getCategory().getName());
			newTransactionDTO.setWalletName(tr.getWallet().getName());
			newTransactionDTO.setTransactionType(tr.getCategory().getTransactionType().getName());
			
			incomesDTO.add(newTransactionDTO);
		}

		
		return incomesDTO;	
	}
	
	public List<TransactionDTO> getAllExpenseTransactions(){
		List<Transaction> expenses = this.transactionRepo.findAllByAmountIsLessThan(new Double(0));
		List<TransactionDTO> expensesDTO = new LinkedList<TransactionDTO>();
		for(Transaction tr : expenses) {
			TransactionDTO newTransactionDTO = new TransactionDTO();
			newTransactionDTO.setAmount(tr.getAmount() * -1);
			newTransactionDTO.setCategoryType(tr.getCategory().getName());
			newTransactionDTO.setWalletName(tr.getWallet().getName());
			newTransactionDTO.setTransactionType(tr.getCategory().getTransactionType().getName());
			
			expensesDTO.add(newTransactionDTO);
		}
	
		
		return expensesDTO;	
	}
	
	public void deleteTransactionById(Long id) {
		System.out.println(this.transactionRepo.findById(id));
		this.transactionRepo.deleteById(id);
	}
	

}
