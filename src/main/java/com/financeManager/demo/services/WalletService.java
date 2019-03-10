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
import com.financeManager.demo.dto.MergeWalletsDTO;
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
	private static final String CHANGE_LIMIT = "update wallets set max_limit = max_limit + ? where id = ?";
	private static final String DELETE_WALLET = "delete from wallets where id = ?";

	@Autowired
	private IWalletDAO walletDao;
	@Autowired
	private IUsersRepository usersRepo;
	@Autowired
	private ITransactionRepository transactionsRepo;
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

		Wallet wallet = this.walletDao.getWalletById(walletId);

		if (!wallet.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to delete this wallet!");
		}

		this.walletDao.deleteWalletById(walletId);
	}

	public void updateWallet(Long walletId, CrudWalletDTO updates, Long userId)
			throws NotExistingWalletException, InvalidWalletEntryException, ForbiddenException {

		Wallet wallet = this.walletDao.getWalletById(walletId);

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
			if(updates.getLimit() < wallet.getBalance()) {
				throw new InvalidWalletEntryException("Invalid wallet settings");
			}
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

		Wallet walletFrom = this.walletDao.getWalletById(transfer.getFromWalletId());
		Wallet walletTo = this.walletDao.getWalletById(transfer.getToWalletId());

		if (!walletFrom.getUser().getId().equals(userId) || !walletTo.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to change this wallet!");
		}

		if (walletFrom.getBalance() < transfer.getAmount()) {
			throw new InsufficientBalanceException("Insufficient account balance.");
		}

		Double amount = transfer.getAmount();

		this.transactionForTransfer(walletFrom.getId(), walletTo.getId(), amount);
		

	}

	private void transactionForTransfer(Long walletFromID, Long walletToID, Double amount)
			throws SQLException, NotExistingWalletException {
		Connection con = null;
		PreparedStatement preparedStatement = null;

		try {
			con = jdbcTemplate.getDataSource().getConnection();
			con.setAutoCommit(false);

			preparedStatement = con.prepareStatement(DECREASE);
			preparedStatement.setDouble(1, amount);
			preparedStatement.setLong(2, walletFromID);
			preparedStatement.executeUpdate();

			preparedStatement = con.prepareStatement(INCREASE);
			preparedStatement.setDouble(1, amount);
			preparedStatement.setLong(2, walletToID);
			preparedStatement.executeUpdate();

			con.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			con.rollback();
			throw new NotExistingWalletException("This wallet doesn't exist!");
		} finally {
			con.setAutoCommit(true);
		}
	}

	public MergeWalletsDTO makeMerge(Long userId, MergeWalletsDTO merge)
			throws NotExistingWalletException, ForbiddenException, SQLException {

		Wallet firstWallet = this.walletDao.getWalletById(merge.getFirstWalletId());
		Wallet secondWallet = this.walletDao.getWalletById(merge.getSecondWalletId());

		if (!firstWallet.getUser().getId().equals(userId) || !secondWallet.getUser().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to change this wallet!");
		}

		if (firstWallet.getId() > secondWallet.getId()) {
			Wallet helper = firstWallet;
			firstWallet = secondWallet;
			secondWallet = helper;

			merge.setFirstWalletId(firstWallet.getId());
			merge.setSecondWalletId(secondWallet.getId());
		}

		Connection con = null;
		PreparedStatement preparedStatement = null;

		Wallet finalWallet = secondWallet;

		try {
			con = jdbcTemplate.getDataSource().getConnection();
			con.setAutoCommit(false);

			this.transactionsRepo.findAllByWalletId(firstWallet.getId()).stream().forEach(transaction -> {
				transaction.setWallet(finalWallet);
				transactionsRepo.saveAndFlush(transaction);
			});

			preparedStatement = con.prepareStatement(INCREASE);
			preparedStatement.setDouble(1, firstWallet.getBalance());
			preparedStatement.setLong(2, secondWallet.getId());
			preparedStatement.executeUpdate();

			preparedStatement = con.prepareStatement(CHANGE_LIMIT);
			preparedStatement.setDouble(1, firstWallet.getLimit());
			preparedStatement.setLong(2, secondWallet.getId());
			preparedStatement.executeUpdate();

			preparedStatement = con.prepareStatement(DELETE_WALLET);
			preparedStatement.setDouble(1, firstWallet.getId());
			preparedStatement.executeUpdate();

			con.commit();

			return merge;

		} catch (SQLException e) {
			e.printStackTrace();
			con.rollback();
			throw new NotExistingWalletException("This wallet doesn't exist!");
		} finally {
			con.setAutoCommit(true);
		}

	}

}
