package com.financeManager.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financeManager.demo.model.Wallet;

@Repository
public interface IWalletRepository extends JpaRepository<Wallet, Long> {
	List<Wallet> findAllByUserId(Long id);
}
