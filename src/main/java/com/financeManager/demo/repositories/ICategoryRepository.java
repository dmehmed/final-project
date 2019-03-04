package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long>{

}
