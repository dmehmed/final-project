package com.financeManager.demo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.AmountOverviewDTO;
import com.financeManager.demo.dto.BudgetOverviewDTO;
import com.financeManager.demo.dto.CategoryOverviewDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidAmountsEntryException;
import com.financeManager.demo.exceptions.InvalidDateException;
import com.financeManager.demo.exceptions.InvalidTransactionTypeException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.services.StatisticService;
import com.financeManager.demo.services.UserService;

@RestController
@RequestMapping(path = "/stats")
public class StatisticController {

	@Autowired
	private StatisticService statsService;

	@Autowired
	private UserService userService;


	@GetMapping(path = "/overview")
	public AmountOverviewDTO getAmountOverview(@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, DateFormatException, InvalidDateException {

		HttpSession session = request.getSession();
		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.statsService.getOverviewOfUserActivity(user, from, till);
	}

	@GetMapping(path = "/overview/transactionType")
	public CategoryOverviewDTO getOverviewByCategories(@RequestParam(name = "type", required = true) String type,
			@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, DateFormatException,
			InvalidDateException, InvalidTransactionTypeException {

		HttpSession session = request.getSession();
		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		User user = this.userService.getExistingUserById(userId);

		return this.statsService.getOverviewOfUserActivityByCategories(user, from, till, type);
	}

	@GetMapping(path = "/overview/budgets/{id}")
	public BudgetOverviewDTO getBudgetMovementById(@PathVariable Long id,HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingBudgetException, ForbiddenException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long userId = (Long) session.getAttribute(Helper.USER_ID);
		
		return this.statsService.getBudgetMovement(id, userId);
	}
	
	
//	@GetMapping(path = "/categories")
//	public List<OverviewDTO> getOverviewByCategories(
//			@RequestParam(name = "from", required = false) String from,
//			@RequestParam(name = "to", required = false) String to, HttpServletRequest request,
//			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, DateFormatException {
//
//		HttpSession session = request.getSession();
//		Helper.isThereLoggedUser(session);
//
//		Long userId = (Long) session.getAttribute(Helper.USER_ID);
//		User user = this.userService.getExistingUserById(userId);
//
//		return this.statsService.getOverviewOfUserActivity(user, from, to);
//	}

}
