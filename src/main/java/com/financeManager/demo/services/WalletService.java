package com.financeManager.demo.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.IUsersRepository;
import com.financeManager.demo.repositories.IWalletRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletService {
	
	@Autowired
	private IWalletRepository walletRepo;
	@Autowired
	private IWalletDAO walletDao;
	@Autowired
	private IUsersRepository usersRepo;
	
	public void addWalletToUser(CrudWalletDTO newWallet, Long userId) {
		User owner = this.usersRepo.findById(userId).get();
		Wallet wallet = new Wallet(newWallet.getName(),newWallet.getBalance(),newWallet.getLimit(),owner);
		this.walletRepo.save(wallet);
		this.walletDao.addWallet(wallet); 
	}
	
	public WalletDTO getWalletById(Long walletId) throws NotExistingWalletException {
		Wallet wallet = this.walletDao.getWalletById(walletId);
		return new WalletDTO(wallet.getName(),wallet.getBalance(),wallet.getLimit());
	}
	
	public void deleteWalletById(Long walletId) throws NotExistingWalletException {
		
		if(!this.walletDao.deleteWalletById(walletId)) {
			throw new NotExistingWalletException();
		}
		
	}
	
	
	
	
	
}
