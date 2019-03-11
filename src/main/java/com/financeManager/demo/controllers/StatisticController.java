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
import com.financeManager.demo.dto.BudgetMoneyPerDayDTO;
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
	
	/**
	 * Gets account of user oveview for a given period or for all of the time he is using the platform.
	 * @param from - start date
	 * @param till - end date.
	 * @param request
	 * @param response
	 * @return AmountOverviewDTO object containing start and end time, incomes,expense and savings.
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws DateFormatException
	 * @throws InvalidDateException
	 */

	@GetMapping(path = "/overview/period")
	public AmountOverviewDTO getAmountOverview(@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till, HttpServletRequest request,
			HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, DateFormatException, InvalidDateException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getOverviewOfUserActivity(userId, from, till);
	}

		/**
		 * 
		 * Gives overview for a chosen type of transaction - income/expense.
		 * 
		 * @param type - income or expense
		 * @param from - start date
		 * @param till -end date
		 * @param request
		 * @param response
		 * @return CategoryOverviewDTO object containing information for transaction count made by category and their amount
		 * plus some additional info.
		 * @throws UnauthorizedException
		 * @throws NotExistingUserException
		 * @throws DateFormatException
		 * @throws InvalidDateException
		 * @throws InvalidTransactionTypeException
		 */

	@GetMapping(path = "/overview/transactionType")
	public CategoryOverviewDTO getOverviewByCategories(@RequestParam(name = "type", required = true) String type,
			@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, DateFormatException,
			InvalidDateException, InvalidTransactionTypeException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getOverviewOfUserActivityByCategories(userId, from, till, type);
	}
	
	/**
	 * 
	 * Gives the category for which the user is made most expenses in a given period.
	 * @param from - start date
	 * @param till -end date
	 * @param request
	 * @param response
	 * @return CategoryWithMostExpensesDTO containing name of the category and amount spent
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws InvalidDateException
	 * @throws DateFormatException
	 */
	
	
	@GetMapping(path = "/categoryWithMostExpenses")
	public CategoryWithMostExpensesDTO getMostSpendingCategory(
			@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "till", required = false) String till,
			HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingUserException, InvalidDateException, DateFormatException {
		Long userId =	Helper.getLoggedUserId(request);
		return this.statsService.getMostSpendingCategory(userId,from,till);
		
	}

	/**
	 * Gives overview of a chosen budget.
	 * 
	 * @param id - budget id
	 * @param request
	 * @param response
	 * @return BudgetOverviewDTO containing budget ID,transaction count,period of the budget,name of the category of the budget
	 * money spent,final calculation, status of the budget.
	 * @throws UnauthorizedException
	 * @throws NotExistingBudgetException
	 * @throws ForbiddenException
	 * @throws InvalidAmountsEntryException
	 * @throws InvalidDateException
	 * @throws DateFormatException
	 */
	
	@GetMapping(path = "/overview/budgets/{id}")
	public BudgetOverviewDTO getBudgetMovementById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingBudgetException, ForbiddenException,
			InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getBudgetMovement(id, userId);
	}
	
	/**
	 * Gives overview of all budgets of the user.
	 * @param request
	 * @param response
	 * @return List of BudgetOverviewDTOs containing budget ID,transaction count,period of the budget,name of the category of the budget
	 * money spent,final calculation, status of the budget.
	 * @throws UnauthorizedException
	 */

	@GetMapping(path = "/overview/budgets")
	public List<BudgetOverviewDTO> getAllBudgetsStats(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {
		Long userId =	Helper.getLoggedUserId(request);
		return this.statsService.getOverviewForAllBudgets(userId);
	}
	
	
	/**
	 * Gives overview of all wallets of the user.
	 * @param id -  id of the desired wallet.
	 * @param request
	 * @param response
	 * @return List of WalletSummaryDTOs containing id of the walled, name of the wallet, transaction count made from this wallet
	 * total money received and total money paid plus final calculation.
	 * @throws UnauthorizedException
	 * @throws NotExistingWalletException
	 * @throws ForbiddenException
	 */

	@GetMapping(path = "/overview/wallets/{id}")
	public WalletSummaryDTO getWalletSummary(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) throws UnauthorizedException, NotExistingWalletException, ForbiddenException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getSummaryOfWallet(userId, id);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return WalletSummaryDTO containing id of the walled, name of the wallet, transaction count made from this wallet
	 * total money received and total money paid plus final calculation.
	 * @throws UnauthorizedException
	 */
	@GetMapping(path = "/overview/wallets")
	public List<WalletSummaryDTO> getAllWalletsSummary(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException {

		Long userId =	Helper.getLoggedUserId(request);
		return this.statsService.getAllWalletsSummary(userId);
	}

	/**
	 * Gives detailed transaction info by days for a chosen period.
	 * @param period - chosen period - month,year,week.
	 * @param request
	 * @param response
	 * @return List of DayActivityDTOs containing day, total transaction count, and count of all expenses and incomes.
	 * income ans expenses sum and list of transactions.
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws InvalidDateException
	 * @throws InvalidPeriodException
	 */
	
	@GetMapping(path = "/overview/transactions")
	public List<DayActivityDTO> getOverviewByPeriod(@RequestParam(name = "period", required = true) String period,
			HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException, InvalidDateException, InvalidPeriodException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getOverviewOfDayActivity(userId, period);

	}
	/**
	 * Gives info for the most profitable and most expensive month of the user from the start of using this app.
	 * @param request
	 * @param response
	 * @return Gives BestAndWorstMonthOverviewDTO containg the name of the month and the amount spent/received.
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 */

	@GetMapping(path = "/overview/bestAndWorstMonth")
	public BestAndWorseMonthOverviewDTO getBestAndWorstMonth(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException {

		Long userId =	Helper.getLoggedUserId(request);

		return this.statsService.getBestAndWorseMonthOverview(userId);
	}
	
	/**
	 * Financial forecast for a gievn period by days.
	 * @param days = count fo the days.
	 * @param request
	 * @param response
	 * @return MoneyPerDayDTO containing how much money per day you have for the given period.
	 * @throws UnauthorizedException
	 */
	
	@GetMapping(path = "/financialForecast")
	public MoneyPerDayDTO getFinancialForecast(@RequestParam(name ="days") int days,HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException {
		Long userId =	Helper.getLoggedUserId(request);
		String format = df.format(this.statsService.getAverageMoneyPerDay(userId, days));
		return new MoneyPerDayDTO(format,days);

	}
	/**
	 * Gives how much money you can spend for this budget for the end of this budgets period.
	 * @param budgetId - id of the desired budget.
	 * @param request
	 * @param response
	 * @return BudgetMoneyPerDayDTO containing category of the budget, remaning amount and amount per day.
	 * @throws UnauthorizedException
	 * @throws NotExistingBudgetException
	 * @throws InvalidAmountsEntryException
	 * @throws InvalidDateException
	 * @throws DateFormatException
	 */
	
	@GetMapping(path = "/budgetRemainingMoneyPerDay/{budgetId}")
	public BudgetMoneyPerDayDTO getBudgetRemainingMoneyPerDay(@PathVariable Long budgetId, HttpServletRequest request,
			HttpServletResponse response ) throws UnauthorizedException, NotExistingBudgetException, InvalidAmountsEntryException, InvalidDateException, DateFormatException {
		
		Long userId = Helper.getLoggedUserId(request);
		return this.statsService.getBudgetRemainingMoneyPerDay(userId, budgetId);
	}

}
