package com.financeManager.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Transaction;
import com.financeManager.demo.model.User;

@Repository
public interface ITransactionRepository  extends JpaRepository<Transaction, Long> {
//	List<Transaction>findTransactionsBetween(int min,int max);
//	@Query("Select * from transactions where amount > 0")
//	List<Transaction>findPositive();
	List<Transaction> findAllByAmountIsGreaterThan(Double amount);
	List<Transaction> findAllByAmountIsLessThan(Double amount);
	@Query("Select t from Transaction t left join Wallet w on(t.wallet = w.id) where w.user =?1")
	List<Transaction> findAllTransactionsByUser(User us);
	

	List<Transaction> findAllByWalletId(Long id);

	
}
