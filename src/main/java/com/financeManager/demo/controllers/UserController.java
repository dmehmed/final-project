package com.financeManager.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RestController;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.dto.IUsersRepository;
import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.services.UserService;

@RestController
public class UserController {

	@Autowired
	private IUsersRepository userRepo;
	@Autowired 
	private UserService userService;
	

	@GetMapping("/users")
	public List<CreateUserDTO> showMe() {
		System.out.println(userRepo.findAll().size());
		return userRepo.findAll().stream()
				.map(user -> new CreateUserDTO(user.getEmail(), user.getPassword(), user.getUsername()))
				.collect(Collectors.toList());
	}

	
	//change to service
	@PostMapping("/register")
	public String makeAccount(@RequestBody @Valid CreateUserDTO newUser, Errors errors, HttpServletResponse response) {
		if (errors.hasErrors()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		} 
		
		User user = this.userRepo.findByEmail(newUser.getEmail());
		if(user != null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return HttpStatus.BAD_REQUEST.getReasonPhrase();
		}
			User usi = this.userService.makeAccount(newUser);
			return HttpStatus.CREATED.getReasonPhrase() + " " +  usi.getId();
		}
	
	
	@GetMapping("/profile")
	public User getUserProfile(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		
		if (session.getAttribute("userId") == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
		
		long id = (Long) session.getAttribute("userId");
		User usi = null;
		
		try {
			 usi = userService.getExistingUserById(id);
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
		
		return usi;
		
	}

	@PostMapping("/login")
	public void login(@RequestBody LoginDTO user, HttpServletRequest request, HttpServletResponse response) {	
		User u = null;
		try {
			u = userService.getExistingUserByEmail(user.getEmail());
		} catch (NotExistingUserException e) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		if(u == null || !(u.getPassword().equals(user.getPassword()))) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return;
		}	
		response.setStatus(HttpStatus.ACCEPTED.value());
		HttpSession session = request.getSession();
		session.setAttribute("userId", u.getId());

	}

	@GetMapping("/logout")
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
	}
	
	

}
