package com.financeManager.demo.controllers;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.financeManager.demo.dto.ForgottenPasswordDTO;
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
import com.financeManager.demo.exceptions.WrongUsernameException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.services.EmailSender;
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
	/**
	 *  Creating an account.
	 *  
	 * @param newUser - CreateUserDTO containing email, password and username.
	 * @param errors
	 * @param response
	 * @return
	 * @throws SQLException
	 * @throws UserWithThisEmailAlreadyExistsException
	 * @throws ValidationException
	 */
	
	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> makeAccount(@RequestBody @Valid CreateUserDTO newUser, Errors errors,
			HttpServletResponse response)
			throws SQLException, UserWithThisEmailAlreadyExistsException, ValidationException {

		Helper.isThereRequestError(errors, response);
		this.userService.hasUserWithEmail(newUser.getEmail());
		User usi = this.userService.makeAccount(newUser);
		return Helper.createResponse(usi.getId(), "User created!", HttpStatus.CREATED);

	}

	/**
	 * View your profile.
	 * @param request
	 * @param response
	 * @return
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 */
	@GetMapping("/profile")
	public UserDTO getUserProfile(HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, NotExistingUserException {
		Long userId =	Helper.getLoggedUserId(request);
		User usi = userService.getExistingUserById(userId);

		return this.userService.getUserProfile(usi.getId());
	}
	/**
	 * Login into our system.
	 * @param user - LoginDTO contains email and password.
	 * @param errors
	 * @param request
	 * @param response
	 * @return
	 * @throws UnauthorizedException
	 * @throws ValidationException
	 * @throws WrongPasswordException
	 * @throws NotExistingUserException
	 */

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginDTO user, Errors errors,
			HttpServletRequest request, HttpServletResponse response)
			throws UnauthorizedException, ValidationException, WrongPasswordException, NotExistingUserException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();

		if (Helper.isThereAlreadySomeoneLogged(session)) {
			return Helper.createResponse((Long) session.getAttribute("userId"), "You are already logged in!",
					HttpStatus.OK);
		}

		User us = this.userService.login(user);
		session.setAttribute(Helper.USER_ID, us.getId());
		this.walletDAO.loadUserWallets(us.getId());
		this.budgetDAO.loadUserBudgets(us.getId());
		return Helper.createResponse(us.getId(), "Welcome " + us.getUsername() + "!", HttpStatus.OK);

	}
 
	/**
	 * Logout of our system.
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/logout")
	public ResponseEntity<ResponseDTO> logout(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();

		if (!Helper.isThereAlreadySomeoneLogged(session)) {
			return Helper.createResponse(null, "There is nobody logged in!", HttpStatus.NOT_FOUND);
		}

		this.walletDAO.clearUserWallets((Long) session.getAttribute(Helper.USER_ID));
		this.budgetDAO.clearUserBudgets((Long) session.getAttribute(Helper.USER_ID));
		ResponseEntity<ResponseDTO> resp = Helper.createResponse((Long) session.getAttribute("userId"), "Goodbye!",
				HttpStatus.OK);

		session.invalidate();
		return resp;
	}

	/**
	 * Update your profile.
	 * @param updates - UpdateProfileDTO containing all the setting and password and email.
	 * @param errors
	 * @param request
	 * @param response
	 * @return
	 * @throws ValidationException
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws NoSuchSettingsOptionException
	 * @throws DateFormatException
	 */
	
	@PatchMapping(path = "/profile/update", consumes = "application/json")
	public ResponseEntity<ResponseDTO> updateProfile(@RequestBody @Valid UpdateProfileDTO updates, Errors errors,
			HttpServletRequest request, HttpServletResponse response) throws ValidationException, UnauthorizedException,
			NotExistingUserException, NoSuchSettingsOptionException, DateFormatException {

		Helper.isThereRequestError(errors, response);

		Long userId =	Helper.getLoggedUserId(request);
		User usi = userService.getExistingUserById(userId);

		this.userService.updateProfile(usi.getId(), updates);

		return Helper.createResponse(usi.getId(), "Profile successfully changed!", HttpStatus.ACCEPTED);

	}
	
	/**
	 * Implemented soft delete from the database to unregister user from our system.
	 * @param request
	 * @param response
	 * @return
	 * @throws NotExistingUserException
	 * @throws UnauthorizedException
	 */

	@PostMapping("/deactivate")
	public ResponseEntity<ResponseDTO> deactivate(HttpServletRequest request, HttpServletResponse response)
			throws NotExistingUserException, UnauthorizedException {

		HttpSession session = request.getSession();
		Helper.isThereLoggedUser(session);

		Long id = (Long) session.getAttribute(Helper.USER_ID);

		this.userService.softDeleteUser(id);
		this.walletDAO.clearUserWallets(id);
		this.budgetDAO.clearUserBudgets(id);

		session.invalidate();

		return Helper.createResponse(null, "Sorry you felt that!", HttpStatus.NO_CONTENT);
	}
	/**
	 * If you have already unregistered, retrieve your account.
	 * @param lazarus - LoginDTO containing email and password fields.
	 * @param request
	 * @param response
	 * @return
	 * @throws NotExistingUserException
	 * @throws WrongPasswordException
	 * @throws SQLException
	 */
	@PostMapping("/retrieve")
	public ResponseEntity<ResponseDTO> retrieveUser(@RequestBody @Valid LoginDTO lazarus, HttpServletRequest request,
			HttpServletResponse response) throws NotExistingUserException, WrongPasswordException, SQLException {
		this.userService.retrieveUser(lazarus);
		User lazar = this.userService.getExistingUserByEmail(lazarus.getEmail());
		return Helper.createResponse(lazar.getId(), "You have risen from the deleted!", HttpStatus.OK);
	}

	/**
	 * Send an email to the email of the user with his new password.
	 * @param user - Takes the email and username for verification.
	 * @param errors
	 * @param response
	 * @param request
	 * @return
	 * @throws ValidationException
	 * @throws UnauthorizedException
	 * @throws NotExistingUserException
	 * @throws WrongUsernameException
	 */
	@PostMapping(path = "/forgottenpassword")
	public ResponseEntity<ResponseDTO> sendNewPass(@RequestBody @Valid ForgottenPasswordDTO user, Errors errors,
			HttpServletResponse response, HttpServletRequest request)
			throws ValidationException, UnauthorizedException, NotExistingUserException, WrongUsernameException {

		Helper.isThereRequestError(errors, response);
		HttpSession session = request.getSession();
		if (Helper.isThereAlreadySomeoneLogged(session)) {
			return Helper.createResponse((Long) session.getAttribute("userId"),
					"You are already logged in, you haven't forgotten your password!", HttpStatus.BAD_REQUEST);
		}

		User owner = userService.getExistingUserByEmail(user.getEmail());
		if (!owner.getUsername().equals(user.getUsername())) {
			throw new WrongUsernameException();
		}

		String newPass = EmailSender.generateCommonLangPassword();
		EmailSender.SendEmail(user.getEmail(), newPass);
		owner.setPassword(DigestUtils.sha256Hex(newPass));
		this.userService.saveUserInRepo(owner);

		return Helper.createResponse(owner.getId(), "Your password is restored", HttpStatus.ACCEPTED);
	}
}
