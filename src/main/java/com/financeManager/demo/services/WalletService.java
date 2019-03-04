package com.financeManager.demo.services;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.InvalidWalletException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.IUsersRepository;

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
	private IWalletDAO walletDao;
	@Autowired
	private IUsersRepository usersRepo;

	public void addWalletToUser(CrudWalletDTO newWallet, Long userId) throws InvalidWalletException {
		User owner = this.usersRepo.findById(userId).get();

		if (newWallet.getName() == null) {
			throw new InvalidWalletException("Invalid wallet name!");
		}

		if (newWallet.getLimit() != null && newWallet.getBalance() != null
				&& newWallet.getLimit().longValue() < newWallet.getBalance().longValue()) {
			throw new InvalidWalletException("Invalid wallet settings");
		}

		Wallet wallet = new Wallet(newWallet.getName(), newWallet.getBalance(), newWallet.getLimit(), owner);
		this.walletDao.addWallet(wallet);
	}

	public WalletDTO getWalletById(Long walletId) throws NotExistingWalletException {
		Wallet wallet = this.walletDao.getWalletById(walletId);
		return new WalletDTO(wallet.getName(), wallet.getBalance(), wallet.getLimit());
	}

	public void deleteWalletById(Long walletId) throws NotExistingWalletException {

		if (!this.walletDao.deleteWalletById(walletId)) {
			throw new NotExistingWalletException();
		}

	}

	public void updateWallet(Long walletId, @Valid CrudWalletDTO updates)
			throws NotExistingWalletException, InvalidWalletException {

		try {
			this.walletDao.getWalletById(walletId);
		} catch (NotExistingWalletException e) {
			throw new NotExistingWalletException();
		}

		if (updates.getName() != null) {
			this.walletDao.getWalletById(walletId).setName(updates.getName());
		}

		if (updates.getLimit() != null && updates.getBalance() != null
				&& updates.getLimit().longValue() < updates.getBalance().longValue()) {
			throw new InvalidWalletException("Invalid wallet settings");
		}

		if (updates.getBalance() != null) {
			this.walletDao.getWalletById(walletId).setBalance(updates.getBalance());
		}

		if (updates.getLimit() != null) {
			this.walletDao.getWalletById(walletId).setLimit(updates.getLimit());
		}

		this.walletDao.saveUpdatedWallet(walletId);

	}

	public List<CrudWalletDTO> getAllUserWallets(Long userId) {
		List<Wallet> wallets = this.walletDao.getAllUserWallets(userId);

		if (wallets == null) {
			return new LinkedList<CrudWalletDTO>();
		}

		return wallets.stream()
				.map(wallet -> new CrudWalletDTO(wallet.getName(), wallet.getBalance(), wallet.getLimit()))
				.collect(Collectors.toList());
	}

}
