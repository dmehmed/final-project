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

import com.financeManager.demo.dto.BudgetDTO;
import com.financeManager.demo.dto.CrudBudgetDTO;
import com.financeManager.demo.exceptions.InvalidBudgetEntryException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.services.BudgetService;

@RestController
@RequestMapping(path = "/budgets")
public class BudgetController {

	@Autowired
	private BudgetService budgetService;

	@GetMapping
	public List<BudgetDTO> getBudgets(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if(!Helper.isThereLoggedUser(response, session)) {
				return new LinkedList<BudgetDTO>();
		}
		
		Long userId = (Long) session.getAttribute(Helper.USER_ID);

		List<BudgetDTO> userBudgets = this.budgetService.getAllUserWallets(userId);

		if (budgetService == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return new LinkedList<BudgetDTO>();
		}

		response.setStatus(HttpStatus.OK.value());
		return userBudgets;

	}
	
	
	@GetMapping(path = "/{id}")  
	public BudgetDTO giveBudgetById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) {

		HttpSession session = request.getSession();

		if(!Helper.isThereLoggedUser(response, session)) {
			return null;
		}
	
		
		try {
			return this.budgetService.getBudgetById(id);
		} catch (NotExistingBudgetException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
	}
	
	@DeleteMapping(path = "/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteBudgetById(@PathVariable Long id, HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();

		if(!Helper.isThereLoggedUser(response, session)) {
			return;
		}
		
		try {
			this.budgetService.deleteBudgetById(id);
		} catch (NotExistingBudgetException e) {
			e.printStackTrace();
			return;
		}
	}

	@PostMapping("/create")
	public String createNewBudget(@RequestBody @Valid CrudBudgetDTO newBudget, Errors errors,HttpServletRequest request,
			HttpServletResponse response) 
	{
		
		
		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			return HttpStatus.UNAUTHORIZED.getReasonPhrase();
		}
		
		Long userId = (Long) session.getAttribute(Helper.USER_ID);

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
	

	
	@PatchMapping(path = "/update/{id}")
	public String update(@RequestBody @Valid CrudBudgetDTO updateBudget ,Errors errors,@PathVariable Long id, 
			HttpServletRequest request, HttpServletResponse response) {
		
		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}
		
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			return HttpStatus.UNAUTHORIZED.getReasonPhrase();
		}
		
		try {
			this.budgetService.updateBudget(updateBudget, id);
		} catch (NotExistingBudgetException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return HttpStatus.NOT_FOUND.getReasonPhrase();
		} catch (InvalidBudgetEntryException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}
		
		response.setStatus(HttpStatus.ACCEPTED.value());
		return "Update " + HttpStatus.ACCEPTED.getReasonPhrase();

	}

}
