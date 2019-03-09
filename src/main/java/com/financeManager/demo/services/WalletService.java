package com.financeManager.demo.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.dto.TransferDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidWalletEntryException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.Transaction;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.ICategoryRepository;
import com.financeManager.demo.repositories.ITransactionRepository;
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

	private static final Long INITIAL_FUNDING = new Long(4);
	private static final String DECREASE = "update wallets set balance = balance - ? where id = ?";
	private static final String INCREASE = "update wallets set balance = balance + ? where id = ?";

	@Autowired
	private IWalletDAO walletDao;
	@Autowired
	private IUsersRepository usersRepo;
	@Autowired
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	@Autowired
	private ICategoryRepository categoryRepo;
	@Autowired
	private ITransactionRepository transactionRepo;

	public Long addWalletToUser(CrudWalletDTO newWallet, Long userId) throws InvalidWalletEntryException {
		User owner = this.usersRepo.findById(userId).get();

		if (newWallet.getName() == null) {
			throw new InvalidWalletEntryException("Invalid wallet name!");
		}

		if ((newWallet.getLimit() != null && newWallet.getBalance() != null)
				&& (newWallet.getLimit().longValue() < newWallet.getBalance().longValue())) {
			throw new InvalidWalletEntryException("Invalid wallet settings");
		}

		Wallet wallet = new Wallet(newWallet.getName(), newWallet.getBalance(), newWallet.getLimit(), owner);		
		Transaction tr = new Transaction(newWallet.getBalance(), "initial funding", wallet, categoryRepo.findById(INITIAL_FUNDING).get());
		this.walletDao.addWallet(wallet);
		transactionRepo.save(tr);
		return wallet.getId();
	}

	public WalletDTO getWalletById(Long walletId, Long userId) throws NotExistingWalletException, ForbiddenException {
		Wallet wallet = this.walletDao.getWalletById(walletId);

		if (!wallet.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to view this wallet!");
		}

		return new WalletDTO(wallet.getId(), wallet.getName(), wallet.getBalance(), wallet.getLimit());
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

	public List<WalletDTO> getAllUserWallets(Long userId) {
		List<Wallet> wallets = this.walletDao.getAllUserWallets(userId);

		if (wallets == null) {
			return new LinkedList<WalletDTO>();
		}

		return wallets.stream()
				.map(wallet -> new WalletDTO(wallet.getId(), wallet.getName(), wallet.getBalance(), wallet.getLimit()))
				.collect(Collectors.toList());
	}

	public void makeTransfer(Long userId, TransferDTO transfer)
			throws NotExistingWalletException, ForbiddenException, InsufficientBalanceException, SQLException {
		Wallet walletFrom = null;
		Wallet walletTo = null;

		try {
			walletFrom = this.walletDao.getWalletById(transfer.getFromWalletId());
			walletTo = this.walletDao.getWalletById(transfer.getToWalletId());
		} catch (NotExistingWalletException e) {
			throw new NotExistingWalletException("Not existing wallet!");
		}

		if (!walletFrom.getUser().getId().equals(userId) || !walletTo.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to change this wallet!");
		}

		if (walletFrom.getBalance() < transfer.getAmount()) {
			throw new InsufficientBalanceException("Insufficient account balance.");
		}

		Connection con = null;
		PreparedStatement preparedStatement = null;

		try {
			con = jdbcTemplate.getDataSource().getConnection();
			con.setAutoCommit(false);

			preparedStatement = con.prepareStatement(DECREASE);
			preparedStatement.setDouble(1, transfer.getAmount());
			preparedStatement.setLong(2, walletFrom.getId());
			preparedStatement.executeUpdate();

			preparedStatement = con.prepareStatement(INCREASE);
			preparedStatement.setDouble(1, transfer.getAmount());
			preparedStatement.setLong(2, walletTo.getId());
			preparedStatement.executeUpdate();

			con.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			con.rollback();
			throw new NotExistingWalletException("Not existing wallet!");
		} finally {
			con.setAutoCommit(true);
		}

	}

}
