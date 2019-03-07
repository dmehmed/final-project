package com.financeManager.demo.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import com.financeManager.demo.dto.TransactionDTO;

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

	public static boolean isThereRequestError(Errors errors, HttpServletResponse response) {

		if (errors.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			System.out.println(errors.getAllErrors());
			return true;
		}
		return false;

	}

	public static boolean isThereLoggedUser(HttpServletResponse response, HttpSession session) {
		if (session == null || session.getAttribute(USER_ID) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return false;
		}
		return true;
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
