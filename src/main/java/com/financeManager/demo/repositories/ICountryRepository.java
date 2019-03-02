package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Country;

@Repository
public interface ICountryRepository extends JpaRepository<Country, Long> {
	
}
