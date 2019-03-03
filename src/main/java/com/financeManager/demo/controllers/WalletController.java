package com.financeManager.demo.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.WalletDTO;

@RestController
@RequestMapping(path = "/wallets")
public class WalletController {
	
	private static final String USER_ID = "userId";

	@Autowired
	private IWalletDAO walletDAO;

	@GetMapping
	public List<WalletDTO> getWallets(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		
		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new LinkedList<WalletDTO>();
		}
		
		long userId = (Long) session.getAttribute(USER_ID);
		List<WalletDTO> userWallets =  this.walletDAO.getAllUserWallets(userId).stream()
				.map(wallet -> new WalletDTO(wallet.getName(), wallet.getBalance(), wallet.getLimit()))
				.collect(Collectors.toList());
		
		if(userWallets == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<WalletDTO>();
		} 
		
		response.setStatus(HttpStatus.OK.value());
		return userWallets;
		
	}
	
	
}
