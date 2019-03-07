package com.financeManager.demo.dao;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.model.Currency;
import com.financeManager.demo.repositories.ICurrencyRepository;

@Component
public class CurrencyDAO implements ICurrencyDAO {

	@Autowired
	private ICurrencyRepository currencyRepo;
	private List<Currency> currencies;

	@Override
	public List<Currency> getAll() {
		return this.currencies;
	}

	@Override
	public Currency getById(Long id) throws NoSuchSettingsOptionException {
		try {
			return this.currencies.stream().filter(country -> country.getId().equals(id)).findFirst().get();
		} catch (NoSuchElementException e) {
			throw new NoSuchSettingsOptionException("This currency option does not exist!");
		}
	}

	@Autowired
	private void setCurrencies() {
		this.currencies = currencyRepo.findAll();
	}

}