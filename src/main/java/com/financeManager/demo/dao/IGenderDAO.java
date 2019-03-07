package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.model.Gender;

public interface IGenderDAO {
	
	public List<Gender> getAll();

	public Gender getById(Long id) throws NoSuchSettingsOptionException;
	
}
