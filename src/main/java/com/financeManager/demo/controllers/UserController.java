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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dao.IBudgetDAO;
import com.financeManager.demo.dao.IWalletDAO;
import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.dto.UpdateProfileDTO;
import com.financeManager.demo.dto.UserDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.UserWithThisEmailAlreadyExistsException;
import com.financeManager.demo.exceptions.WrongPasswordException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.services.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private IWalletDAO walletDAO;

	@Autowired
	private IBudgetDAO budgetDAO;

//	@GetMapping("/users")
//	public List<CreateUserDTO> showMe() {
//		System.out.println(userRepo.findAll().size());
//		return userRepo.findAll().stream()
//				.map(user -> new CreateUserDTO(user.getEmail(), user.getPassword(), user.getUsername()))
//				.collect(Collectors.toList());
//	}

	// we need to move that in other controller
//	@GetMapping("/categories")
//	public List<CurrencyDTO> listAllCategories(HttpServletResponse response) {
//
//		return this.categoryDao.getAll().stream().map(category -> new CurrencyDTO(category.getId(), category.getName()))
//				.collect(Collectors.toList());
//
//	}
	
	//we need to move that in other controller
//	@Autowired
//	private ITransactionTypeDAO typeDAO;
//	
//	@GetMapping("/transactionTypes")
//	public List<TransactionTypeDTO> listAllTransactionTypes(HttpServletResponse response){
//		return this.typeDAO.getAll().stream().map(type -> new TransactionTypeDTO(type.getId(), type.getName())).collect(Collectors.toList());
//	}

	@PostMapping("/register")
	public String makeAccount(@RequestBody @Valid CreateUserDTO newUser, Errors errors, HttpServletResponse response)
			throws SQLException {

		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}

		if (this.userService.hasUserWithEmail(newUser.getEmail())) {
			response.setStatus(HttpStatus.CONFLICT.value());
			return HttpStatus.CONFLICT.getReasonPhrase();
		}

		User usi;
		try {
			usi = this.userService.makeAccount(newUser);
		} catch (UserWithThisEmailAlreadyExistsException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.CONFLICT.value());
			return HttpStatus.CONFLICT.getReasonPhrase();
		}

		response.setStatus(HttpStatus.CREATED.value());
		return HttpStatus.CREATED.getReasonPhrase() + " " + usi.getId();

	}

	@GetMapping("/profile")
	public UserDTO getUserProfile(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			return null;
		}
		;

		Long id = (Long) session.getAttribute(Helper.USER_ID);
		User usi = null;

		try {
			usi = userService.getExistingUserById(id);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			e.printStackTrace();
			return null;
		}

		return this.userService.getUserProfile(usi.getId());
	}

	@PostMapping("/login")
	public String login(@RequestBody @Valid LoginDTO user, Errors errors, HttpServletRequest request,
			HttpServletResponse response) {

		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}

		User us = null;
		try {
			try {
				us = this.userService.login(user);
			} catch (NotExistingUserException e) {

				e.printStackTrace();
				response.setStatus(HttpStatus.NOT_FOUND.value());
				return (HttpStatus.NOT_FOUND.getReasonPhrase());
			}
		} catch (WrongPasswordException e) {
			e.printStackTrace();

			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}

		response.setStatus(HttpStatus.ACCEPTED.value());
		HttpSession session = request.getSession();
		session.setAttribute(Helper.USER_ID, us.getId());
		this.walletDAO.loadUserWallets(us.getId());
		this.budgetDAO.loadUserBudgets(us.getId());
		return "Login " + HttpStatus.ACCEPTED.getReasonPhrase();

	}

	@GetMapping("/logout")
	public void logout(HttpServletRequest request) {

		HttpSession session = request.getSession();

		this.walletDAO.clearUserWallets((Long) session.getAttribute(Helper.USER_ID));
		this.budgetDAO.clearUserBudgets((Long) session.getAttribute(Helper.USER_ID));

		session.invalidate();
	}

	@PatchMapping(path = "/profile/update", consumes = "application/json")
	public String updateProfile(@RequestBody @Valid UpdateProfileDTO updates, Errors errors, HttpServletRequest request,
			HttpServletResponse response) {

		HttpSession session = request.getSession();

		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}

		if (!Helper.isThereLoggedUser(response, session)) {
			return HttpStatus.UNAUTHORIZED.getReasonPhrase();
		}

		Long id = (Long) session.getAttribute(Helper.USER_ID);
		User usi = null;

		try {
			usi = userService.getExistingUserById(id);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			e.printStackTrace();
			return HttpStatus.NOT_FOUND.getReasonPhrase();
		}

		try {

			this.userService.updateProfile(usi.getId(), updates);

		} catch (DateFormatException | NoSuchSettingsOptionException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}

		response.setStatus(HttpStatus.OK.value());
		return HttpStatus.OK.getReasonPhrase();

	}

	@GetMapping("/deactivate")
	public String deactivate(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (!Helper.isThereLoggedUser(response, session)) {
			return HttpStatus.UNAUTHORIZED.getReasonPhrase() + " Could not delete!";
		}
		;

		Long id = (Long) session.getAttribute(Helper.USER_ID);
		try {
			this.userService.softDeleteUser(id);
			this.walletDAO.clearUserWallets(id);
			this.budgetDAO.clearUserBudgets(id);

			session.invalidate();
		} catch (NotExistingUserException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return HttpStatus.NOT_FOUND.getReasonPhrase();
		}

		response.setStatus(HttpStatus.NO_CONTENT.value());
		return HttpStatus.NO_CONTENT.getReasonPhrase();
	}

	@PostMapping("/retrieve")
	public String retrieveUser(@RequestBody @Valid LoginDTO lazarus, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			this.userService.retrieveUser(lazarus);
		} catch (NotExistingUserException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "User " + HttpStatus.NOT_FOUND.getReasonPhrase();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return HttpStatus.BAD_REQUEST.getReasonPhrase() + " - wrong password!";
		} catch (SQLException e) {
			e.printStackTrace();

			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "Database " + HttpStatus.NOT_FOUND.getReasonPhrase();
		}

		response.setStatus(HttpStatus.ACCEPTED.value());
		return HttpStatus.ACCEPTED.getReasonPhrase() + " you have risen from the deleted!";

	}

//	@GetMapping("/deleted")
//	public int getDeletedUsers(){
//		return this.userService.listOfDeleted();
//	}
}
