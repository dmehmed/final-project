package com.financeManager.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Transaction;

@Repository
public interface ITransactionRepository  extends JpaRepository<Transaction, Long> {
//	List<Transaction>findTransactionsBetween(int min,int max);
//	@Query("Select * from transactions where amount > 0")
//	List<Transaction>findPositive();
	List<Transaction> findAllByAmountIsGreaterThan(Double amount);
	List<Transaction> findAllByAmountIsLessThan(Double amount);

}
