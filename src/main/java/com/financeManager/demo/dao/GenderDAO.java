package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.Gender;
import com.financeManager.demo.repositories.IGenderRepository;

@Component
public class GenderDAO implements IGenderDAO {

	@Autowired
	private IGenderRepository genderRepo;
	private List<Gender> genders;

	@Override
	public List<Gender> getAll() throws SQLException {
		return this.genders;
	}

	@Override
	public Gender getById(Long id) {
		return this.genders.stream().filter(country -> country.getId().equals(id)).findFirst().get();
	}
	
	@Autowired
	public void setGenders() {
		this.genders = genderRepo.findAll();
	}

}