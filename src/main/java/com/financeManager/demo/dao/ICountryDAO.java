	package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.model.Country;

public interface ICountryDAO {
	public List<Country> getAll();
	public Country getById(Long id);
}
