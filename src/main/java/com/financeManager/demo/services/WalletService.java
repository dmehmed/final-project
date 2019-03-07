package com.financeManager.demo.services;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidWalletEntryException;
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

	public Long addWalletToUser(CrudWalletDTO newWallet, Long userId) throws InvalidWalletEntryException {
		User owner = this.usersRepo.findById(userId).get();

		if (newWallet.getName() == null) {
			throw new InvalidWalletEntryException("Invalid wallet name!");
		}

		if (newWallet.getLimit() != null && newWallet.getBalance() != null
				&& newWallet.getLimit().longValue() < newWallet.getBalance().longValue()) {
			throw new InvalidWalletEntryException("Invalid wallet settings");
		}

		Wallet wallet = new Wallet(newWallet.getName(), newWallet.getBalance(), newWallet.getLimit(), owner);
		return this.walletDao.addWallet(wallet);
	}

	public WalletDTO getWalletById(Long walletId, Long userId) throws NotExistingWalletException, ForbiddenException {
		Wallet wallet = this.walletDao.getWalletById(walletId);

		if (!wallet.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to view this wallet!");
		}

		return new WalletDTO(wallet.getName(), wallet.getBalance(), wallet.getLimit());
	}

	public void deleteWalletById(Long walletId, Long userId) throws NotExistingWalletException, ForbiddenException {

		Wallet wallet = null;

		try {
			wallet = this.walletDao.getWalletById(walletId);
		} catch (NotExistingWalletException e) {
			throw new NotExistingWalletException("Not existing wallet!");
		}

		if (!wallet.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to delete this wallet!");
		}

		this.walletDao.deleteWalletById(walletId);
	}

	public void updateWallet(Long walletId, CrudWalletDTO updates, Long userId)
			throws NotExistingWalletException, InvalidWalletEntryException, ForbiddenException {

		Wallet wallet = null;

		try {
			wallet = this.walletDao.getWalletById(walletId);
		} catch (NotExistingWalletException e) {
			throw new NotExistingWalletException("Not existing wallet!");
		}

		if (!wallet.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to update this wallet!");
		}

		if (updates.getName() != null) {
			this.walletDao.getWalletById(walletId).setName(updates.getName());
		}

		if (updates.getLimit() != null && updates.getBalance() != null
				&& updates.getLimit().longValue() < updates.getBalance().longValue()) {
			throw new InvalidWalletEntryException("Invalid wallet settings");
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
