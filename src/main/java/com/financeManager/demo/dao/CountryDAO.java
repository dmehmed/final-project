package com.financeManager.demo.dao;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.model.Country;
import com.financeManager.demo.repositories.ICountryRepository;

@Component
public class CountryDAO implements ICountryDAO {

	@Autowired
	private ICountryRepository countryRepo;
	private List<Country> countries;

	@Override
	public List<Country> getAll() {
		return this.countries;
	}

	@Override
	public Country getById(Long id) throws NoSuchSettingsOptionException {
		try {
			return this.countries.stream().filter(country -> country.getId().equals(id)).findFirst().get();
		} catch (NoSuchElementException e) {
			throw new NoSuchSettingsOptionException("This country option does not exist!");
		}
	}

	@Autowired
	private void setCountries() {
		this.countries = this.countryRepo.findAll();
	}

}
