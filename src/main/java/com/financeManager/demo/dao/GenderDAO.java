package com.financeManager.demo.dao;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.model.Gender;
import com.financeManager.demo.repositories.IGenderRepository;

@Component
public class GenderDAO implements IGenderDAO {

	@Autowired
	private IGenderRepository genderRepo;
	private List<Gender> genders;

	@Override
	public List<Gender> getAll() {
		return this.genders;
	}

	@Override
	public Gender getById(Long id) throws NoSuchSettingsOptionException {
		try {
			return this.genders.stream().filter(country -> country.getId().equals(id)).findFirst().get();
		} catch (NoSuchElementException e) {
			throw new NoSuchSettingsOptionException("This gender option does not exist!");
		}
	}

	@Autowired
	private void setGenders() {
		this.genders = genderRepo.findAll();
	}

}