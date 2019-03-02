package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import com.financeManager.demo.model.Country;

public interface ICountryDAO {

	int ID_COLUMN = 1;
	int NAME_COLUMN = 2;

	public List<Country> getAll() throws SQLException;
	public Country getById(Long id);
	
}
