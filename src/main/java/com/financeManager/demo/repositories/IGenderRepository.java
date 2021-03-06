package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Gender;

@Repository
public interface IGenderRepository extends JpaRepository<Gender, Long> {
	
}
