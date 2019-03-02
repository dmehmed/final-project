package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.Currency;
import com.financeManager.demo.repositories.ICurrencyRepository;

@Component
public class CurrencyDAO implements ICurrencyDAO {

	@Autowired
	private ICurrencyRepository currencyRepo;
	private List<Currency> currencies;

	@Override
	public List<Currency> getAll() throws SQLException {
		return this.currencies;
	}

	@Override
	public Currency getById(Long id) {
		return this.currencies.stream().filter(country -> country.getId().equals(id)).findFirst().get();
	}
	
	@Autowired
	public void setCurrencies() {
		this.currencies = currencyRepo.findAll();
	}

}