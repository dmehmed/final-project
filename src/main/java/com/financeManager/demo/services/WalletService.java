package com.financeManager.demo.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.model.Transaction;
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
	private IUsersRepository usersRepo;
	
	public void addWalletToUser(CrudWalletDTO newWallet, Long userId) {
		User owner = this.usersRepo.findById(userId).get();
		
		Wallet wallet = new Wallet(newWallet.getName(),newWallet.getBalance(),newWallet.getLimit(),owner);
		walletRepo.save(wallet);
	}
	
	
}
