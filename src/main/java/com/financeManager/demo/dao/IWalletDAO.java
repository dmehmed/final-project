package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.model.Wallet;

public interface IWalletDAO {
	
	Wallet getWalletById(Long id);
	void addWallet(Wallet wallet);
	List<Wallet> getAllUserWallets(Long userId);
	void loadUserWallets(Long userId);
	void clearUserWallets(Long userId);
	
}
