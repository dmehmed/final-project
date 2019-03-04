package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.model.Currency;

public interface ICurrencyDAO {

	public List<Currency> getAll();
	public Currency getById(Long id);
	
}