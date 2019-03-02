package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.Country;
import com.financeManager.demo.repositories.ICountryRepository;

@Component
public class CountryDAO implements ICountryDAO {

	@Autowired
	private ICountryRepository countryRepo;
	private List<Country> countries;

	@Override
	public List<Country> getAll() throws SQLException {
		return this.countries;
	}

	@Override
	public Country getById(Long id) {
		return this.countries.stream().filter(country -> country.getId().equals(id)).findFirst().get();
	}
	
	@Autowired
	public void setCountries() {
		this.countries = countryRepo.findAll();
	}

}
