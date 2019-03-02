package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import com.financeManager.demo.model.Country;

public interface ICountryDAO {

	public List<Country> getAllCountries() throws SQLException;
	public Country getCountryById(Long id);
	
}
