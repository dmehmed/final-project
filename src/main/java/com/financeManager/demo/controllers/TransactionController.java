package com.financeManager.demo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.CreateTransactionDTO;
import com.financeManager.demo.exceptions.InvalidTransactionEntryException;
import com.financeManager.demo.services.TransactionService;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@PostMapping("/create")
	public String createTransaction(@RequestBody @Valid CreateTransactionDTO newTransaction, Errors errors, 
			HttpServletRequest request, HttpServletResponse response) {
		
		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			return HttpStatus.UNAUTHORIZED.getReasonPhrase();
		}
		
		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		
		try {
			this.transactionService.createTransaction(newTransaction, userId);
		} catch (InvalidTransactionEntryException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return e.getMessage();
		}
		
		
		response.setStatus(HttpStatus.CREATED.value());
		return HttpStatus.CREATED.getReasonPhrase();
		
	}
	
}
