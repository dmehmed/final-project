package com.financeManager.demo.controllers;

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

import com.financeManager.demo.dto.BudgetDTO;
import com.financeManager.demo.dto.CrudBudgetDTO;
import com.financeManager.demo.dto.ResponseDTO;
import com.financeManager.demo.exceptions.AlreadyExistingBudget;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidBudgetEntryException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.exceptions.ValidationException;
import com.financeManager.demo.services.BudgetService;

@RestController
@RequestMapping(path = "/budgets")
public class BudgetController {

	@Autowired
	private BudgetService budgetService;

	/**
	 * 
	 * Find all budgets of the logged in user
	 * @param request 
	 * @param response
	 * @return List of DTOs of Bugdet class.
	 * @throws UnauthorizedException
	 */
	
	@GetMapping
	public List<BudgetDTO> getBudgets(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);

		List<BudgetDTO> userBudgets = this.budgetService.getAllUserBugdets(userId);

		response.setStatus(HttpStatus.OK.value());
		return userBudgets;

	}

	@GetMapping(path = "/{id}")
	public BudgetDTO giveBudgetById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingBudgetException, ForbiddenException {

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute("userId");
		return this.budgetService.getBudgetById(userId, id);
	}

	@DeleteMapping(path = "/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBudgetById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingBudgetException, ForbiddenException {

		HttpSession session = request.getSession();
		Helper.isThereLoggedUser(session);
		Long userId = (Long) session.getAttribute("userId");
		this.budgetService.deleteBudgetById(userId, id);

	}

	@PostMapping("/create")
	public ResponseEntity<ResponseDTO> createNewBudget(@RequestBody @Valid CrudBudgetDTO newBudget, Errors errors,
			HttpServletRequest request, HttpServletResponse response)
			throws ValidationException, UnauthorizedException, InvalidBudgetEntryException, AlreadyExistingBudget {

		Helper.isThereRequestError(errors, response);

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);

		return Helper.createResponse(this.budgetService.addBudgetToUser(newBudget, userId),
				"Budget added successfully!", HttpStatus.CREATED);

	}

	@PatchMapping(path = "/update/{id}")
	public ResponseEntity<ResponseDTO> update(@RequestBody @Valid CrudBudgetDTO updateBudget, Errors errors,
			@PathVariable Long id, HttpServletRequest request, HttpServletResponse response)
			throws NotExistingBudgetException, InvalidBudgetEntryException, ForbiddenException, UnauthorizedException,
			ValidationException {

		Helper.isThereRequestError(errors, response);

		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);

		this.budgetService.updateBudget(updateBudget, userId, id);
		
		return Helper.createResponse(id,"Budget updated successfully!", HttpStatus.ACCEPTED);
	}


}
