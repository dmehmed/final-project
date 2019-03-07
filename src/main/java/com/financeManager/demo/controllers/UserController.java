package com.financeManager.demo.controllers;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.financeManager.demo.dto.ResponseDTO;
import com.financeManager.demo.dto.UpdateProfileDTO;
import com.financeManager.demo.dto.UserDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.UnauthorizedException;
import com.financeManager.demo.exceptions.UserWithThisEmailAlreadyExistsException;
import com.financeManager.demo.exceptions.ValidationException;
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

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> makeAccount(@RequestBody @Valid CreateUserDTO newUser, Errors errors,
			HttpServletResponse response)
			throws SQLException, UserWithThisEmailAlreadyExistsException, ValidationException {

		Helper.isThereRequestError(errors, response);
		this.userService.hasUserWithEmail(newUser.getEmail());
		User usi = this.userService.makeAccount(newUser);
		return Helper.createResponse(usi.getId(), "User created!", HttpStatus.CREATED);

	}

	@GetMapping("/profile")
	public UserDTO getUserProfile(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException {
		HttpSession session = request.getSession();

		Helper.isThereLoggedUser(session);

		Long id = (Long) session.getAttribute(Helper.USER_ID);
		User usi = userService.getExistingUserById(id);

		return this.userService.getUserProfile(usi.getId());
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginDTO user, Errors errors,
			HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, ValidationException, WrongPasswordException, NotExistingUserException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();

		if (Helper.isThereAlreadySomeoneLogged(session)) {
			return Helper.createResponse((Long) session.getAttribute("userId"), "You are already logged in",
					HttpStatus.OK);
		}

		User us = this.userService.login(user);
		session.setAttribute(Helper.USER_ID, us.getId());
		this.walletDAO.loadUserWallets(us.getId());
		this.budgetDAO.loadUserBudgets(us.getId());
		return Helper.createResponse(us.getId(), "Welcome " + us.getUsername() + " !", HttpStatus.OK);

	}

	@GetMapping("/logout")
	public ResponseEntity<ResponseDTO> logout(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();

		if (!Helper.isThereAlreadySomeoneLogged(session)) {
			return Helper.createResponse(null, "There is nobody logged in!", HttpStatus.NOT_FOUND);
		}

//		this.walletDAO.clearUserWallets((Long) session.getAttribute(Helper.USER_ID));
//		this.budgetDAO.clearUserBudgets((Long) session.getAttribute(Helper.USER_ID));

		session.invalidate();
		return Helper.createResponse((Long) session.getAttribute("userId"), "Goodbye!", HttpStatus.OK);
	}

	@PatchMapping(path = "/profile/update", consumes = "application/json")
	public ResponseEntity<ResponseDTO> updateProfile(@RequestBody @Valid UpdateProfileDTO updates, Errors errors,
			HttpServletRequest request, HttpServletResponse response) throws ValidationException, UnauthorizedException,
			NotExistingUserException, NoSuchSettingsOptionException, DateFormatException {

		Helper.isThereRequestError(errors, response);

		HttpSession session = request.getSession();
		Helper.isThereLoggedUser(session);

		Long id = (Long) session.getAttribute(Helper.USER_ID);
		User usi = userService.getExistingUserById(id);

		this.userService.updateProfile(usi.getId(), updates);

		return Helper.createResponse(usi.getId(), "Profile successfully changed!", HttpStatus.ACCEPTED);

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

	@PostMapping(path = "/forgottenpassword")
	public String sendNewPass(@RequestBody @Valid ForgottenPasswordDTO user, Errors errors,
			HttpServletResponse response, HttpServletRequest request) {

		if (Helper.isThereRequestError(errors, response)) {
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}

		HttpSession session = request.getSession();

		if (Helper.isThereLoggedUser(response, session)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return HttpStatus.UNAUTHORIZED.getReasonPhrase();
		}

		User owner;
		try {
			owner = userService.getExistingUserByEmail(user.getEmail());

			if (!owner.getUsername().equals(user.getUsername())) {
				throw new WrongUsernameException();
			}
			String newPass = EmailSender.generateCommonLangPassword();
			EmailSender.SendEmail(user.getEmail(), newPass);

			owner.setPassword(DigestUtils.sha256Hex(newPass));
			this.userService.saveUserInRepo(owner);

		} catch (NotExistingUserException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return HttpStatus.NOT_FOUND.getReasonPhrase();
		} catch (WrongUsernameException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return HttpStatus.FORBIDDEN.getReasonPhrase();
		}

		response.setStatus(HttpStatus.ACCEPTED.value());

		return "Password retreival: " + HttpStatus.ACCEPTED.getReasonPhrase();
	}

}
