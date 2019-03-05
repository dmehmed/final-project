package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.model.TransactionType;

public interface ITransactionTypeDAO {
	public List<TransactionType> getAll();
	public TransactionType getById(Long id);
}
