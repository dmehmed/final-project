package com.financeManager.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.TransactionType;

@Repository
public interface ITransactionTypeRepository extends JpaRepository<TransactionType, Long> {

}
