package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import com.financeManager.demo.model.Category;

public interface ICategoryDao {
	public List<Category> getAll() throws SQLException;
	public Category getById(Long id);
}
