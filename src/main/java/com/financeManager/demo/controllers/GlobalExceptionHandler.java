package com.financeManager.demo.controllers;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.InsufficientBalanceException;
import com.financeManager.demo.exceptions.InvalidAmountsEntryException;
import com.financeManager.demo.exceptions.InvalidBudgetEntryException;
import com.financeManager.demo.exceptions.InvalidDateException;
import com.financeManager.demo.exceptions.InvalidTransactionEntryException;
import com.financeManager.demo.exceptions.InvalidWalletEntryException;
import com.financeManager.demo.exceptions.InvalidWalletException;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.exceptions.NotExistingBudgetException;
import com.financeManager.demo.exceptions.NotExistingTransactionException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.NotExistingWalletException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.exceptions.UserWithThisEmailAlreadyExistsException;
import com.financeManager.demo.exceptions.WrongPasswordException;
import com.financeManager.demo.exceptions.WrongUsernameException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	class ErrorMessageDTO {
		private Timestamp timestamp;
		private int status;
		private String message;
		private String details;
	}

	@ExceptionHandler(UnauthorizedException.class)
	public final ResponseEntity<ErrorMessageDTO> unauthorizedProblem(Exception ex) {
		ErrorMessageDTO errorMessage = new ErrorMessageDTO(Timestamp.valueOf(LocalDateTime.now()),
				HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage());

		return new ResponseEntity<ErrorMessageDTO>(errorMessage, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({ NotExistingUserException.class, NotExistingWalletException.class,
			NotExistingBudgetException.class, NotExistingTransactionException.class,
			NoSuchSettingsOptionException.class })
	public final ResponseEntity<ErrorMessageDTO> notExistingResourseProblem(Exception ex) {
		ErrorMessageDTO errorMessage = new ErrorMessageDTO(Timestamp.valueOf(LocalDateTime.now()),
				HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());

		return new ResponseEntity<ErrorMessageDTO>(errorMessage, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ DateFormatException.class, InvalidAmountsEntryException.class,
			InvalidBudgetEntryException.class, InvalidDateException.class, InvalidTransactionEntryException.class,
			InvalidWalletEntryException.class, InvalidWalletException.class, WrongPasswordException.class,
			WrongUsernameException.class, UserWithThisEmailAlreadyExistsException.class,
			InsufficientBalanceException.class })
	public final ResponseEntity<ErrorMessageDTO> badInputProblem(Exception ex) {
		ErrorMessageDTO errorMessage = new ErrorMessageDTO(Timestamp.valueOf(LocalDateTime.now()),
				HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());

		return new ResponseEntity<ErrorMessageDTO>(errorMessage, HttpStatus.BAD_REQUEST);
	}

}
