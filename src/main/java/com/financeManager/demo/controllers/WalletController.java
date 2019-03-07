package com.financeManager.demo.controllers;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.financeManager.demo.dto.WalletDTO;
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

	@GetMapping
	public List<CrudWalletDTO> getWallets(HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(USER_ID);
		List<CrudWalletDTO> userWallets = this.walletService.getAllUserWallets(userId);

		if (userWallets == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<CrudWalletDTO>();
		}

		response.setStatus(HttpStatus.OK.value());
		return userWallets;
	}

	@PatchMapping(path = "/update/{id}")
	public String updateWallet(@RequestBody @Valid CrudWalletDTO updates, Errors errors, @PathVariable Long id,
			HttpServletRequest request, HttpServletResponse response)
			throws ValidationException, UnauthorizedException, NotExistingWalletException, InvalidWalletEntryException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		this.walletService.updateWallet(id, updates);

		response.setStatus(HttpStatus.ACCEPTED.value());
		return "Update " + HttpStatus.ACCEPTED.getReasonPhrase();

	}

	@PostMapping("/create")
	public String createNewWallet(@RequestBody @Valid CrudWalletDTO newWallet, Errors errors,
			HttpServletRequest request, HttpServletResponse response)
			throws ValidationException, UnauthorizedException, InvalidWalletEntryException {

		Helper.isThereRequestError(errors, response);

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(USER_ID);

		this.walletService.addWalletToUser(newWallet, userId);
		response.setStatus(HttpStatus.CREATED.value());
		return HttpStatus.CREATED.getReasonPhrase();

	}

	@GetMapping("/{id}")
	public WalletDTO giveWalletById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingWalletException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		WalletDTO wallet = this.walletService.getWalletById(id);
		return wallet;
	}

	@DeleteMapping(path = "/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteWalletById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingWalletException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		this.walletService.deleteWalletById(id);

	}

}
