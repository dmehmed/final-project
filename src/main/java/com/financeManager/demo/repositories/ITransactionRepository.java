package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Transaction;

@Repository
public interface ITransactionRepository  extends JpaRepository<Transaction, Long> {

}
