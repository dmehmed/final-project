package com.financeManager.demo.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import lombok.Getter;


public abstract class Helper {
	
	

	 static final String USER_ID = "userId";
	
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
}
