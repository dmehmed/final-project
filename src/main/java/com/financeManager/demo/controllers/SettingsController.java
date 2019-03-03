package com.financeManager.demo.controllers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dao.ICountryDAO;
import com.financeManager.demo.dao.ICurrencyDAO;
import com.financeManager.demo.dao.IGenderDAO;
import com.financeManager.demo.dto.CountryDTO;
import com.financeManager.demo.dto.CurrencyDTO;
import com.financeManager.demo.dto.GenderDTO;

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
	public List<CountryDTO> getAllCountries(HttpServletResponse response){

		try {
			return this.countryDAO.getAll().stream().map(country -> new CountryDTO(country.getId(), country.getName())).collect(Collectors.toList());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<CountryDTO>();
		}
	}
	
	@GetMapping("/currencies")
	public List<CurrencyDTO> getAllCurrencies(HttpServletResponse response){

		try {
			return this.currencyDAO.getAll().stream().map(currency -> new CurrencyDTO(currency.getId(), currency.getType())).collect(Collectors.toList());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<CurrencyDTO>();
		}
	}
	
	@GetMapping("/genders")
	public List<GenderDTO> getAllGenders(HttpServletResponse response){

		try {
			return this.genderDAO.getAll().stream().map(gender -> new GenderDTO(gender.getId(), gender.getName())).collect(Collectors.toList());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<GenderDTO>();
		}
	}
	
}