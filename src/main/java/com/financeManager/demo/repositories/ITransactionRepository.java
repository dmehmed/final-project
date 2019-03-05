package com.financeManager.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Transaction;

@Repository
public interface ITransactionRepository  extends JpaRepository<Transaction, Long> {
//	List<Transaction>findTransactionsBetween(int min,int max);
	@Query("Select * from transactions where amount > 0")
	List<Transaction>findPositive();


}
