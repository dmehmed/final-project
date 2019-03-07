package com.financeManager.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dao.ICountryDAO;
import com.financeManager.demo.dao.ICurrencyDAO;
import com.financeManager.demo.dao.IGenderDAO;
import com.financeManager.demo.dto.CountryDTO;
import com.financeManager.demo.dto.CurrencyDTO;
import com.financeManager.demo.dto.GenderDTO;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.model.Country;
import com.financeManager.demo.model.Currency;
import com.financeManager.demo.model.Gender;

@RestController
@RequestMapping(path = "/settings")
public class SettingsController {

	@Autowired
	private ICountryDAO countryDAO;

	@Autowired
	private ICurrencyDAO currencyDAO;

	@Autowired
	private IGenderDAO genderDAO;

	@GetMapping("/countries")
	public List<CountryDTO> getAllCountries(HttpServletResponse response) {

		return this.countryDAO.getAll().stream().map(country -> new CountryDTO(country.getId(), country.getName()))
				.collect(Collectors.toList());
	}

	@GetMapping("/countries/{id}")
	public CountryDTO getCountryById(@PathVariable Long id, HttpServletResponse response)
			throws NoSuchSettingsOptionException {

		Country country = this.countryDAO.getById(id);

		return new CountryDTO(country.getId(), country.getName());
	}

	@GetMapping("/currencies")
	public List<CurrencyDTO> getAllCurrencies(HttpServletResponse response) {

		return this.currencyDAO.getAll().stream().map(currency -> new CurrencyDTO(currency.getId(), currency.getType()))
				.collect(Collectors.toList());
	}

	@GetMapping("/currencies/{id}")
	public CurrencyDTO getCurrencyById(@PathVariable Long id, HttpServletResponse response)
			throws NoSuchSettingsOptionException {

		Currency currency = this.currencyDAO.getById(id);

		return new CurrencyDTO(currency.getId(), currency.getType());
	}

	@GetMapping("/genders")
	public List<GenderDTO> getAllGenders(HttpServletResponse response) {

		return this.genderDAO.getAll().stream().map(gender -> new GenderDTO(gender.getId(), gender.getName()))
				.collect(Collectors.toList());
	}

	@GetMapping("/genders/{id}")
	public GenderDTO getGenderById(@PathVariable Long id, HttpServletResponse response)
			throws NoSuchSettingsOptionException {

		Gender gender = this.genderDAO.getById(id);

		return new GenderDTO(gender.getId(), gender.getName());
	}

}