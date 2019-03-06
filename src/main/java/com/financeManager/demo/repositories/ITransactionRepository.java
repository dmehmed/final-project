package com.financeManager.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Transaction;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;

@Repository
public interface ITransactionRepository  extends JpaRepository<Transaction, Long> {
//	List<Transaction>findTransactionsBetween(int min,int max);
//	@Query("Select * from transactions where amount > 0")
//	List<Transaction>findPositive();
	
	@Query("Select t from Transaction t left join Wallet w on(t.wallet = w.id) where w.user =?1 and t.amount > ?2")
	List<Transaction> findAllTransactionsByUserWhereAmountIsGreaterThan(User us,Double amount);
	@Query("Select t from Transaction t left join Wallet w on(t.wallet = w.id) where w.user =?1 and t.amount < ?2")
	List<Transaction> findAllTransactionsByUserWhereAmountIsLessThan(User us,Double amount);
	@Query("Select t from Transaction t left join Wallet w on(t.wallet = w.id) where w.user =?1 and t.amount > ?2 and t.amount < ?3")
	List<Transaction> findAllTransactionsByUserWhereAmountIsBetween(User us,Double min,Double max);
	
	
	@Query("Select t from Transaction t left join Wallet w on(t.wallet = w.id) where w.user =?1")
	List<Transaction> findAllTransactionsByUser(User us);
	List<Transaction> findAllByWalletId(Long id);

	
}
