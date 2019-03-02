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
import com.financeManager.demo.dto.CountryDTO;

@RestController
@RequestMapping(path = "/settings")
public class SettingsController {

	
	@Autowired
	private ICountryDAO countryDAO;
	
	@GetMapping("/countries")
	public List<CountryDTO> getAllCountries(HttpServletResponse response){

		try {
			return this.countryDAO.getAll().stream().map(country -> new CountryDTO(country.getId(), country.getName())).collect(Collectors.toList());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
			return new LinkedList<CountryDTO>();
		}
	}
	
	@GetMapping("/currencies")
	public List<CountryDTO> getAllCurrencies(HttpServletResponse response){

		try {
			return this.countryDAO.getAll().stream().map(country -> new CountryDTO(country.getId(), country.getName())).collect(Collectors.toList());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
			return new LinkedList<CountryDTO>();
		}
	}
	
}