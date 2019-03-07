package com.financeManager.demo.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.validation.Errors;

import com.financeManager.demo.dto.TransactionDTO;
import com.financeManager.demo.exceptions.ForbiddenException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.exceptions.ValidationException;


public abstract class Helper {

	private static final String ASC = "asc";

	static final String USER_ID = "userId";

	private static final String WALLET = "wallet";
	private static final String CATEGORY = "category";
	private static final String TYPE = "type";
	private static final String AMOUNT = "amount";
	private static final String DATE = "date";
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

	public static void isThereRequestError(Errors errors, HttpServletResponse response) throws ValidationException {

		if (errors.hasErrors()) {
			throw new ValidationException(errors.getFieldError().getDefaultMessage());  
		}
		
	}

	public static void isThereLoggedUser(HttpSession session) throws UnauthorizedException {
		if (session == null || session.getAttribute("userId") == null) {		
			throw new UnauthorizedException("You are not logged in!");
		}

	}
	public static void isThisTheCorrectUser(HttpSession session, Long userId,Long resourcesUserId) throws UnauthorizedException, ForbiddenException  {
		Helper.isThereLoggedUser(session);
		if(!userId.equals(resourcesUserId)) {
			throw new ForbiddenException("Can't touch this!");
		}

	}
	

	public static Comparator<TransactionDTO> giveComparatorByCriteria(String criteria, String orderBy) {

		if(criteria == null) {
			criteria = DATE;
		}
		if(orderBy == null) {
			orderBy = ASC;
		}
		

		int coeff;

		if (orderBy.equals(ASC)) {
			coeff = 1;
		} else {
			coeff = -1;
		}

		switch (criteria) {
		case DATE:
			return new Comparator<TransactionDTO>() {
				@Override
				public int compare(TransactionDTO o1, TransactionDTO o2) {

					return coeff * o1.getCreationDate().compareTo(o2.getCreationDate());

				}
			};
		case AMOUNT:
			return new Comparator<TransactionDTO>() {
				@Override
				public int compare(TransactionDTO o1, TransactionDTO o2) {
					return coeff * o1.getAmount().compareTo(o2.getAmount());

				}
			};
		case TYPE:
			return new Comparator<TransactionDTO>() {
				@Override
				public int compare(TransactionDTO o1, TransactionDTO o2) {

					return coeff * o1.getTransactionType()

							.compareTo(o2.getTransactionType());
				}
			};
		case CATEGORY:
			return new Comparator<TransactionDTO>() {
				@Override
				public int compare(TransactionDTO o1, TransactionDTO o2) {

					return coeff * o1.getCategoryType().compareTo(o2.getCategoryType());

				}
			};
		case WALLET:
			return new Comparator<TransactionDTO>() {
				@Override
				public int compare(TransactionDTO o1, TransactionDTO o2) {

					return coeff * o1.getWalletName().compareTo(o2.getWalletName());

				}
			};

		default:
			return new Comparator<TransactionDTO>() {
				@Override
				public int compare(TransactionDTO o1, TransactionDTO o2) {

					return coeff * o1.getCreationDate().compareTo(o2.getCreationDate());

				}
			};
		}
	}

	public static Timestamp parseStringToTimeStamp(String date) {
		return date != null ? Timestamp.valueOf(LocalDate.parse(date, dateTimeFormatter).atStartOfDay()) : null;	
	}

	public static LocalDateTime parseStringToLocalDateTime(String date) {
		return date != null ? LocalDate.parse(date, dateTimeFormatter).atStartOfDay() : null;	
	}
}
