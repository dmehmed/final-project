package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import com.financeManager.demo.model.Currency;

public interface ICurrencyDAO {

	int ID_COLUMN = 1;
	int NAME_COLUMN = 2;

	public List<Currency> getAll() throws SQLException;
	public Currency getById(Long id);
	
}

