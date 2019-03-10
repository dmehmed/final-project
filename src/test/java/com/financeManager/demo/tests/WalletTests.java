package com.financeManager.demo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.dto.MergeWalletsDTO;
import com.financeManager.demo.dto.TransferDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidWalletEntryException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.model.Wallet;
import com.financeManager.demo.repositories.IWalletRepository;
import com.financeManager.demo.services.UserService;
import com.financeManager.demo.services.WalletService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WalletTests {

	@Autowired
	private WalletService walletService;
	@Autowired
	private IWalletRepository walletRepo;
	@Autowired
	private IWalletDAO walletDao;
	@Autowired
	private UserService userService;

	@Test
	public void testCreateUserWallet() throws NotExistingUserException, InvalidWalletEntryException, NotExistingWalletException {
		
		User user = this.userService.getExistingUserById(3L);
		walletDao.loadUserWallets(user.getId());
		
		CrudWalletDTO newWallet = new CrudWalletDTO("JUnitTestWallet", 1340.0, 2000.0);
		
		int countInRepoBefore = walletRepo.findAllByUserId(user.getId()).size();
		int countInDaoBefore = walletDao.getAllUserWallets(user.getId()).size();
		
		Long newWalletId = walletService.addWalletToUser(newWallet, user.getId());
		
		int countInRepoAfter = walletRepo.findAllByUserId(user.getId()).size();
		int countInDaoAfter = walletDao.getAllUserWallets(user.getId()).size();
		
		assertTrue(countInRepoBefore + 1 == countInRepoAfter);
		assertTrue(countInDaoBefore + 1 == countInDaoAfter);
		assertNotNull(walletRepo.findById(newWalletId).get());
		assertNotNull(walletDao.getWalletById(newWalletId));
		
		this.walletDao.clearUserWallets(user.getId());
	}
	
	@Test
	public void testGetAllUserWallets() throws NotExistingUserException {
		
		User user = this.userService.getExistingUserById(3L);
		this.walletDao.loadUserWallets(user.getId());
		
		int walletCountInDao = this.walletService.getAllUserWallets(user.getId()).size();
		int walletCountInRepo = this.walletRepo.findAllByUserId(user.getId()).size();		
		
		assertTrue(walletCountInDao == walletCountInRepo);
		
		this.walletDao.clearUserWallets(user.getId());
	}
	
	@Test
	public void testUpdateUserWallet() throws NotExistingUserException, NotExistingWalletException, InvalidWalletEntryException, ForbiddenException {
		User user = this.userService.getExistingUserById(3L);
		this.walletDao.loadUserWallets(user.getId());
		
		CrudWalletDTO updateWallet = new CrudWalletDTO();
		updateWallet.setName("Update wallet");
		updateWallet.setLimit(3300.0);
		
		Long walletId = 61L;
		
		this.walletService.updateWallet(walletId, updateWallet, user.getId());
		
		Wallet walletInRepo = this.walletRepo.findById(walletId).get();
		Wallet walletInDao = this.walletDao.getWalletById(walletId);
		
		assertEquals(walletInRepo.getName(), updateWallet.getName());
		assertEquals(walletInDao.getName(), updateWallet.getName());
		
		this.walletDao.clearUserWallets(user.getId());
	}
	
	@Test
	public void testViewUserWallet() throws NotExistingUserException, NotExistingWalletException, ForbiddenException {
		User user = this.userService.getExistingUserById(3L);
		this.walletDao.loadUserWallets(user.getId());
		
		Long walletId = 61L;
		
		WalletDTO wallet = this.walletService.getWalletById(walletId, user.getId());
		
		assertEquals(wallet.getId(), walletId);
		
		this.walletDao.clearUserWallets(user.getId());
	}
	
	@Test
	public void testDeleteUserWallet() throws NotExistingUserException, NotExistingWalletException, ForbiddenException {
		User user = this.userService.getExistingUserById(3L);
		this.walletDao.loadUserWallets(user.getId());
		
		Long walletId = 84L;
		
		this.walletService.deleteWalletById(walletId, user.getId());
		
		boolean isThereWalletInRepo = this.walletRepo.findById(walletId).isPresent();
		boolean isThereWalletInDao = this.walletDao.getAllUserWallets(user.getId()).stream().filter(wallet -> wallet.getId().equals(walletId)).findFirst().isPresent();
		
		assertFalse(isThereWalletInRepo);
		assertFalse(isThereWalletInDao);
		
		this.walletDao.clearUserWallets(user.getId());
	}

	@Test
	public void testMergeUserWallets()
			throws NotExistingUserException, NotExistingWalletException, ForbiddenException, SQLException {
		User user = this.userService.getExistingUserById(3L);
		this.walletDao.loadUserWallets(user.getId());

		MergeWalletsDTO mergeWalletsInfo = new MergeWalletsDTO(80L, 82L);
		
		Wallet firstWallet = this.walletDao.getWalletById(mergeWalletsInfo.getFirstWalletId());
		Wallet secondWallet = this.walletDao.getWalletById(mergeWalletsInfo.getSecondWalletId());
		
		Double newBalance = firstWallet.getBalance() + secondWallet.getBalance();
		Double newLimit = firstWallet.getLimit() + secondWallet.getLimit();

		this.walletService.makeMerge(user.getId(), mergeWalletsInfo);

		boolean isThereFirstWalletInRepo = this.walletRepo.findById(mergeWalletsInfo.getFirstWalletId()).isPresent();
		boolean isThereFirstWalletInDao = this.walletDao.getAllUserWallets(user.getId()).stream()
				.filter(wallet -> wallet.getId().equals(mergeWalletsInfo.getFirstWalletId())).findFirst().isPresent();
		
		Wallet mergeResult = this.walletDao.getWalletById(mergeWalletsInfo.getSecondWalletId());
		
		assertFalse(isThereFirstWalletInRepo);
		assertFalse(isThereFirstWalletInDao);
		assertEquals(newBalance, mergeResult.getBalance());
		assertEquals(newLimit, mergeResult.getLimit());
		
		this.walletDao.clearUserWallets(user.getId());
	}

	@Test
	public void testTransferAmountBetweenWallets() throws NotExistingUserException, NotExistingWalletException, ForbiddenException, InsufficientBalanceException, SQLException {
		User user = this.userService.getExistingUserById(3L);
		this.walletDao.loadUserWallets(user.getId());
		
		TransferDTO transferInfo = new TransferDTO(340.0, 82L, 86L);
		
		Wallet firstWallet = this.walletDao.getWalletById(transferInfo.getFromWalletId());
		Wallet secondWallet = this.walletDao.getWalletById(transferInfo.getToWalletId());
		
		Double expectInFirst = firstWallet.getBalance() - transferInfo.getAmount();
		Double expectInSecond = secondWallet.getBalance() + transferInfo.getAmount();
		
		this.walletService.makeTransfer(user.getId(), transferInfo);
		
		firstWallet = this.walletDao.getWalletById(transferInfo.getFromWalletId());
		secondWallet = this.walletDao.getWalletById(transferInfo.getToWalletId());
		
		Double newFirstWalletBalance = firstWallet.getBalance();
		Double newSecondWalletBalance = secondWallet.getBalance();
		
		assertEquals(expectInFirst, newFirstWalletBalance);
		assertEquals(expectInSecond, newSecondWalletBalance);
		
		this.walletDao.clearUserWallets(user.getId());
	}
	
}
