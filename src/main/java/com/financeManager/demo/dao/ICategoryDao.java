package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.model.Category;

public interface ICategoryDao {
	public List<Category> getAll();
	public Category getById(Long id);
}
