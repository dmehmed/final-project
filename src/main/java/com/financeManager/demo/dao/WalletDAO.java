package com.financeManager.demo.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.IWalletRepository;

@Component
public class WalletDAO implements IWalletDAO {

	@Autowired
	private IWalletRepository walletRepo;
	private List<Wallet> wallets = new LinkedList<Wallet>();

	@Override
	public Wallet getWalletById(Long id) throws NotExistingWalletException {
		try{
		return this.wallets.stream().filter(wallet -> wallet.getId().equals(id)).findAny().get();
		} catch (NoSuchElementException e) { 
			throw new NotExistingWalletException();
		}
	}

	@Override
	public void loadUserWallets(Long userId) {
		this.wallets.addAll(this.walletRepo.findAllByUserId(userId));
	}

	@Override
	public List<Wallet> getAllUserWallets(Long userId) {
		return this.wallets.stream().filter(wallet -> wallet.getUser().getId().equals(userId))
				.collect(Collectors.toList());
	}

	@Override
	public void addWallet(Wallet wallet) {
		this.walletRepo.save(wallet);
		this.wallets.add(wallet);
	}

	@Override
	public void clearUserWallets(Long userId) {
		this.wallets = this.wallets.stream().filter(wallet -> !wallet.getUser().getId().equals(userId))
				.collect(Collectors.toList());
	}

	@Override
	public boolean deleteWalletById(Long walletId) {
		try {
			Wallet w = this.wallets.stream().
					filter(wallet -> wallet.getId().equals(walletId)).findFirst().get();
			this.wallets.remove(w);
			this.walletRepo.deleteById(walletId);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	@Override
	public void saveUpdatedWallet(Long walletId) throws NotExistingWalletException {
			this.walletRepo.save(this.getWalletById(walletId));
	}

}
