package com.financeManager.demo.dao;

import java.util.List;

import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.Wallet;

public interface IWalletDAO {
	
	Wallet getWalletById(Long id) throws NotExistingWalletException;
	Long addWallet(Wallet wallet);
	List<Wallet> getAllUserWallets(Long userId);
	void loadUserWallets(Long userId);
	void clearUserWallets(Long userId);
	void deleteWalletById(Long walletId);
	void saveUpdatedWallet(Long walletId) throws NotExistingWalletException;

}
