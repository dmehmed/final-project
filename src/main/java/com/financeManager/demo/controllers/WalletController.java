package com.financeManager.demo.controllers;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.dto.MergeWalletsDTO;
import com.financeManager.demo.dto.ResponseDTO;
import com.financeManager.demo.dto.TransferDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidWalletEntryException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.exceptions.ValidationException;
import com.financeManager.demo.services.WalletService;

@RestController
@RequestMapping(path = "/wallets")
public class WalletController {

	private static final String USER_ID = "userId";

	@Autowired
	private WalletService walletService;

	/**
	 * Method return user wallets in list of WalletDTO.
	 * 
	 * @param request
	 * @param response
	 * @return List of WalletDTO
	 * @throws UnauthorizedException
	 */

	@GetMapping
	public List<WalletDTO> getWallets(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(USER_ID);
		List<WalletDTO> userWallets = this.walletService.getAllUserWallets(userId);

		response.setStatus(HttpStatus.OK.value());
		return userWallets;
	}

	/**
	 * Method receive information for updating user wallet stored in CrudWalletDTO.
	 * Search and update a user wallet with the id given in the URL. We update only
	 * fields that are not null in the CrudWalletDTO.
	 * 
	 * @param updates
	 * @param errors
	 * @param id
	 * @param request
	 * @param response
	 * @return ResponseEntity for result of request
	 * @throws ValidationException
	 * @throws UnauthorizedException
	 * @throws NotExistingWalletException
	 * @throws InvalidWalletEntryException
	 * @throws ForbiddenException
	 */

	@PatchMapping(path = "/update/{id}")
	public ResponseEntity<ResponseDTO> updateWallet(@RequestBody @Valid CrudWalletDTO updates, Errors errors,
			@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws ValidationException,
			UnauthorizedException, NotExistingWalletException, InvalidWalletEntryException, ForbiddenException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute(USER_ID);

		this.walletService.updateWallet(id, updates, userId);

		return Helper.createResponse(id, "Wallet successfully changed!", HttpStatus.ACCEPTED);

	}

	/**
	 * 
	 * Method receive information for creating user wallet stored in CrudWalletDTO.
	 * Create a user wallet based on the information provided.
	 * 
	 * @param newWallet
	 * @param errors
	 * @param request
	 * @param response
	 * @return ResponseEntity for result of request
	 * @throws ValidationException
	 * @throws UnauthorizedException
	 * @throws InvalidWalletEntryException
	 */

	@PostMapping("/create")
	public ResponseEntity<ResponseDTO> createNewWallet(@RequestBody @Valid CrudWalletDTO newWallet, Errors errors,
			HttpServletRequest request, HttpServletResponse response)
			throws ValidationException, UnauthorizedException, InvalidWalletEntryException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute(USER_ID);
		Long walletId = this.walletService.addWalletToUser(newWallet, userId);

		return Helper.createResponse(walletId, "Wallet successfully created!", HttpStatus.CREATED);
	}

	/**
	 * 
	 * Method search and return for a user wallet with the id given in the URL.
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return WalletDTO
	 * @throws UnauthorizedException
	 * @throws NotExistingWalletException
	 * @throws ForbiddenException
	 */

	@GetMapping("/{id}")
	public WalletDTO giveWalletById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingWalletException, ForbiddenException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute(USER_ID);

		WalletDTO wallet = this.walletService.getWalletById(id, userId);
		return wallet;
	}

	/**
	 * 
	 * Method search and delete user wallet with the id given in the URL.
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @throws UnauthorizedException
	 * @throws NotExistingWalletException
	 * @throws ForbiddenException
	 */

	@DeleteMapping(path = "/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteWalletById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingWalletException, ForbiddenException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute(USER_ID);

		this.walletService.deleteWalletById(id, userId);
	}

	/**
	 * 
	 * Method receive information for transfer amount from one user wallet to
	 * another stored in TransferDTO. Search wallets with given id in the
	 * TransferDTO. Make transaction to update amounts of both wallets - take amount
	 * given in the TransferDTO from the first wallet and add the same amount to the
	 * second.
	 * 
	 * @param transfer
	 * @param errors
	 * @param request
	 * @param response
	 * @return ResponseEntity for result of request
	 * @throws ValidationException
	 * @throws UnauthorizedException
	 * @throws NotExistingWalletException
	 * @throws ForbiddenException
	 * @throws InsufficientBalanceException
	 * @throws SQLException
	 */

	@PostMapping(path = "/transfer")
	public ResponseEntity<ResponseDTO> transferAmount(@RequestBody @Valid TransferDTO transfer, Errors errors,
			HttpServletRequest request, HttpServletResponse response) throws ValidationException, UnauthorizedException,
			NotExistingWalletException, ForbiddenException, InsufficientBalanceException, SQLException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute(USER_ID);

		this.walletService.makeTransfer(userId, transfer);

		return Helper.createResponse(transfer.getToWalletId(),
				"Transfer successfully made from wallet with id " + transfer.getFromWalletId() + "!",
				HttpStatus.ACCEPTED);
	}

	/**
	 * 
	 * Method receive information for merge two user wallets stored in
	 * MergeWalletDTO. Search wallets with given id in the MergeWalletDTO. Make
	 * transaction to merge early created wallet to later created - take balance and limit
	 * from first and add them to the second, after that delete first wallet.
	 * 
	 * @param merge
	 * @param errors
	 * @param request
	 * @param response
	 * @return ResponseEntity for result of request
	 * @throws ValidationException
	 * @throws UnauthorizedException
	 * @throws NotExistingWalletException
	 * @throws ForbiddenException
	 * @throws SQLException
	 */

	@PostMapping(path = "/merge")
	public ResponseEntity<ResponseDTO> mergeWallets(@RequestBody @Valid MergeWalletsDTO merge, Errors errors,
			HttpServletRequest request, HttpServletResponse response) throws ValidationException, UnauthorizedException,
			NotExistingWalletException, ForbiddenException, SQLException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute(USER_ID);

		merge = this.walletService.makeMerge(userId, merge);

		return Helper.createResponse(merge.getSecondWalletId(),
				"Merge successfully made with wallet with id " + merge.getFirstWalletId() + "!", HttpStatus.ACCEPTED);
	}

}
