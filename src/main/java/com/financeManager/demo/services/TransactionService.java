package com.financeManager.demo.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.ICategoryDao;
import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CreateTransactionDTO;
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

	@Autowired
	private ITransactionRepository transactionRepo;

	@Autowired
	private ICategoryDao categoryDAO;

	@Autowired
	private IWalletDAO walletDAO;

	public void createTransaction(CreateTransactionDTO newTransaction, Long userId)
			throws InvalidTransactionEntryException {

		Wallet userWallet;
		Category transactionCategory;

		try {
			userWallet = walletDAO.getWalletById(newTransaction.getWalletId());
		} catch (NotExistingWalletException e) {
			e.printStackTrace();
			throw new InvalidTransactionEntryException("There is no such wallet", e);
		}

		try {
			transactionCategory = categoryDAO.getById(newTransaction.getCategoryId());
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new InvalidTransactionEntryException("Invalid category type");
		}

		Transaction transaction = new Transaction(newTransaction.getAmount(), newTransaction.getDescription(),
				userWallet, transactionCategory);
		this.transactionRepo.save(transaction);
	}

}
