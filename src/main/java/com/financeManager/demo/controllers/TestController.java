package com.financeManager.demo.controllers;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.dto.UserDTO;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.exceptions.UserWithThisEmailAlreadyExistsException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.services.UserService;

@RestController
@RequestMapping(path = "/hello")
public class TestController {

	@Autowired
	private UserService userService;

	@GetMapping("/test")
	public UserDTO getUserProfile(HttpServletRequest request, HttpServletResponse response) throws NotExistingUserException, UnauthorizedException {
		HttpSession session = request.getSession();
		
		this.isThereLoggedUser(response, session);
			
		


		Long id = (Long) session.getAttribute(Helper.USER_ID);
		User usi = null;
		
		System.out.println("zdrrr");

		try {
			usi = userService.getExistingUserById(id);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			e.printStackTrace();
			return null;
		}

		return this.userService.getUserProfile(usi.getId());
	}

	@PostMapping
	public String makeAccount(@RequestBody @Valid CreateUserDTO newUser, Errors errors, HttpServletResponse response)
			throws SQLException {

//		if (Helper.isThereRequestError(errors, response)) {
//			return HttpStatus.BAD_REQUEST.getReasonPhrase();
//		}
//
//		if (this.userService.hasUserWithEmail(newUser.getEmail())) {
//			response.setStatus(HttpStatus.CONFLICT.value());
//			return HttpStatus.CONFLICT.getReasonPhrase();
//		}
//		User usi;
//		try {
//			usi = this.userService.makeAccount(newUser);
//		} catch (UserWithThisEmailAlreadyExistsException e) {
//			e.printStackTrace();
//			response.setStatus(HttpStatus.CONFLICT.value());
//			return HttpStatus.CONFLICT.getReasonPhrase();
//		}

		response.setStatus(HttpStatus.CREATED.value());
		return HttpStatus.CREATED.getReasonPhrase();

	}
	
	
	public boolean isThereLoggedUser(HttpServletResponse response, HttpSession session) throws UnauthorizedException {
		if (session == null || session.getAttribute("userId") == null) {		
			throw new UnauthorizedException("You are not logged in");
		}
		return true;
	}
	
}
