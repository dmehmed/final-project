package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Currency;

@Repository
public interface ICurrencyRepository extends JpaRepository<Currency, Long> {
	
}
