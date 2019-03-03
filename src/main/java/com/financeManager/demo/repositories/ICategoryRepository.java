package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financeManager.demo.model.Category;

public interface ICategoryRepository extends JpaRepository<Category, Long>{

}
