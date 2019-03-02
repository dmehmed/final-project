package com.financeManager.demo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.Country;

@Component
public class CountryDAO implements ICountryDAO {

	private static final int ID_COLUMN = 1;
	private static final int NAME_COLUMN = 2;

	private static final String GET_COUNTRIES = "SELECT * FROM countries";
	@Autowired
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();	
	private List<Country> countries;
	
//	@Autowired
//	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) throws SQLException {
//		this.jdbcTemplate = jdbcTemplate;
//	}
	
	

	@Override
	public List<Country> getAllCountries() throws SQLException {
		
		if(this.countries == null) {
			this.setCountries();
		}
		
		return this.countries;
	}

	@Override
	public Country getCountryById(Long id) {
		return this.countries.stream().filter(country -> country.getId().equals(id)).findFirst().get();
	}
	
	private void setCountries() throws SQLException {
		this.countries = new LinkedList<Country>();
		ResultSet result = this.jdbcTemplate.getDataSource().getConnection().createStatement().executeQuery(GET_COUNTRIES);
		
		while(result.next()) {
			this.countries.add(new Country(result.getLong(ID_COLUMN), result.getString(NAME_COLUMN)));
		}
		
	}

}
