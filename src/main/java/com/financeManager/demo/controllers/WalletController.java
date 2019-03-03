package com.financeManager.demo.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CrudWalletDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.services.WalletService;

@RestController
@RequestMapping(path = "/wallets")
public class WalletController {

	private static final String USER_ID = "userId";

	@Autowired
	private IWalletDAO walletDAO;
	@Autowired
	private WalletService walletService;

	@GetMapping
	public List<CrudWalletDTO> getWallets(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new LinkedList<CrudWalletDTO>();
			
		}

		long userId = (Long) session.getAttribute(USER_ID);

		List<CrudWalletDTO> userWallets = this.walletDAO.getAllUserWallets(userId).stream() // refactor
				.map(wallet -> new CrudWalletDTO(wallet.getName(), wallet.getBalance(), wallet.getLimit()))
				.collect(Collectors.toList());
		
		
		if (userWallets == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<CrudWalletDTO>();
		}
		response.setStatus(HttpStatus.OK.value());
		return userWallets;
	}

	@PostMapping("/create")
	public void createNewWallet(@RequestBody @Valid CrudWalletDTO newWallet, HttpServletRequest request,
			HttpServletResponse response,Errors errors) {
		
		if (errors.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			System.out.println(errors.getAllErrors());
		}
		
		
		HttpSession session = request.getSession();

		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}

		long userId = (Long) session.getAttribute(USER_ID);
		this.walletService.addWalletToUser(newWallet, userId);
		response.setStatus(HttpStatus.CREATED.value());

	}
	
	@GetMapping("/{id}")  
	public WalletDTO giveWalletById(@PathVariable Long id,HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}

		try {
			return this.walletService.getWalletById(id);
		} catch (NotExistingWalletException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
	}

	@DeleteMapping(path = "/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteWalletById(@PathVariable Long id, HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		
		try {
			this.walletService.deleteWalletById(id);
		} catch (NotExistingWalletException e) {
			e.printStackTrace();
			return;
		}
	}
}
