package com.financeManager.demo.controllers;

import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.AmountOverviewDTO;
import com.financeManager.demo.dto.BestAndWorseMonthOverviewDTO;
import com.financeManager.demo.dto.BudgetOverviewDTO;
import com.financeManager.demo.dto.CategoryOverviewDTO;
import com.financeManager.demo.dto.CategoryWithMostExpensesDTO;
import com.financeManager.demo.dto.DayActivityDTO;
import com.financeManager.demo.dto.MoneyPerDayDTO;
import com.financeManager.demo.dto.WalletSummaryDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.InvalidAmountsEntryException;
import com.financeManager.demo.exceptions.InvalidDateException;
import com.financeManager.demo.exceptions.InvalidPeriodException;
import com.financeManager.demo.exceptions.InvalidTransactionTypeException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.services.StatisticService;

@RestController
@RequestMapping(path = "/stats")
public class StatisticController {

	private DecimalFormat df = new DecimalFormat("##.##");
	@Autowired
	private StatisticService statsService;

	@GetMapping(path = "/overview/period")
	public AmountOverviewDTO getAmountOverview(@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, DateFormatException, InvalidDateException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getOverviewOfUserActivity(userId, from, till);
	}



	@GetMapping(path = "/overview/transactionType")
	public CategoryOverviewDTO getOverviewByCategories(@RequestParam(name = "type", required = true) String type,
			@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, DateFormatException,
			InvalidDateException, InvalidTransactionTypeException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getOverviewOfUserActivityByCategories(userId, from, till, type);
	}
	
	
	@GetMapping(path = "/categoryWithMostExpenses")
	public CategoryWithMostExpensesDTO getMostSpendingCategory(String type,
			@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till,
			HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, InvalidDateException, DateFormatException {
		Long userId =	Helper.getLoggedUserId(request);
		return this.statsService.getMostSpendingCategory(userId,from,till);
		
	}

	@GetMapping(path = "/overview/budgets/{id}")
	public BudgetOverviewDTO getBudgetMovementById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingBudgetException, ForbiddenException,
			InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getBudgetMovement(id, userId);
	}

	@GetMapping(path = "/overview/budgets")
	public List<BudgetOverviewDTO> getAllBudgetsStats(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {
		Long userId =	Helper.getLoggedUserId(request);
		return this.statsService.getOverviewForAllBudgets(userId);
	}

	@GetMapping(path = "/overview/wallets/{id}")
	public WalletSummaryDTO getWalletSummary(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingWalletException, ForbiddenException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getSummaryOfWallet(userId, id);
	}

	@GetMapping(path = "/overview/wallets")
	public List<WalletSummaryDTO> getAllWalletsSummary(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {

		Long userId =	Helper.getLoggedUserId(request);
		return this.statsService.getAllWalletsSummary(userId);
	}

	@GetMapping(path = "/overview/transactions")
	public List<DayActivityDTO> getOverviewByPeriod(@RequestParam(name = "period", required = true) String period,
			HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, InvalidDateException, InvalidPeriodException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getOverviewOfDayActivity(userId, period);

	}

	@GetMapping(path = "/overview/bestAndWorstMonth")
	public BestAndWorseMonthOverviewDTO getBestAndWorstMonth(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getBestAndWorseMonthOverview(userId);
	}
	@GetMapping(path = "/financialForecast")

	public MoneyPerDayDTO getFinancialForecast(@RequestParam(name ="days") int days,HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException {
		Long userId =	Helper.getLoggedUserId(request);
		String format = df.format(this.statsService.getAverageMoneyPerDay(userId, days));
		return new MoneyPerDayDTO(format,days);

	}

}
