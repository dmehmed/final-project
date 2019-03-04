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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.BudgetDTO;
import com.financeManager.demo.dto.CrudBudgetDTO;
import com.financeManager.demo.dto.WalletDTO;
import com.financeManager.demo.exceptions.InvalidBudgetEntryException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.services.BudgetService;

@RestController
@RequestMapping(path = "/budgets")
public class BudgetController {

	private static final String USER_ID = "userId";

	@Autowired
	private BudgetService budgetService;

	@GetMapping
	public List<BudgetDTO> getBudgets(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new LinkedList<BudgetDTO>();

		}

		Long userId = (Long) session.getAttribute(USER_ID);

		List<BudgetDTO> userBudgets = this.budgetService.getAllUserWallets(userId);

		if (budgetService == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<BudgetDTO>();
		}

		response.setStatus(HttpStatus.OK.value());
		return userBudgets;

	}

	@PostMapping("/create")
	public String createNewBudget(@RequestBody @Valid CrudBudgetDTO newBudget, HttpServletRequest request,
			HttpServletResponse response, Errors errors) {

		if (errors.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			System.out.println(errors.getAllErrors());
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}

		HttpSession session = request.getSession();

		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return HttpStatus.UNAUTHORIZED.getReasonPhrase();
		}

		Long userId = (Long) session.getAttribute(USER_ID);

		try {
			this.budgetService.addBudgetToUser(newBudget, userId);
			response.setStatus(HttpStatus.CREATED.value());
			return HttpStatus.CREATED.getReasonPhrase();
		} catch (InvalidBudgetEntryException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return e.getMessage();
		}
		

	}
	
	@GetMapping("/{id}")  
	public BudgetDTO giveBudgetById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
		
		try {
			return this.budgetService.getBudgetById(id);
		} catch (NotExistingBudgetException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
	}

}
