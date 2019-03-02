package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import com.financeManager.demo.model.Gender;

public interface IGenderDAO {

	int ID_COLUMN = 1;
	int NAME_COLUMN = 2;

	public List<Gender> getAll() throws SQLException;
	public Gender getById(Long id);
	
}
