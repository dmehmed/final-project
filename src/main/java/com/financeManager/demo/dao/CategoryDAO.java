package com.financeManager.demo.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.model.Category;
import com.financeManager.demo.repositories.ICategoryRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
public class CategoryDAO implements ICategoryDao {
	
	@Autowired
	private ICategoryRepository repo;
	
	private List<Category> categories;
	
	
	
	@Override
	public List<Category> getAll() throws SQLException {
		return this.categories;
	}

	@Override
	public Category getById(Long id) {
		return this.categories.stream().filter(country -> country.getId().equals(id)).findFirst().get();
	}
	
	@Autowired
	public void setCategories() {
		this.categories = this.repo.findAll();
	}

}
