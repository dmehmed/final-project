package com.financeManager.demo.tests;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.WrongPasswordException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.repositories.ISettingsRepository;
import com.financeManager.demo.repositories.IUsersRepository;
import com.financeManager.demo.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTests {
	@Autowired
	private UserService userService;
	@Autowired
	private ISettingsRepository settingsRepo;
	@Autowired
	private IUsersRepository repo;
	
//	@Test
//	public void addUser() throws SQLException, UserWithThisEmailAlreadyExistsException, NoSuchSettingsOptionException {
//		CreateUserDTO newUser = new CreateUserDTO("Mirkata11@abv.bg","Cska1948#","Mirchoni");
//		User user = this.userService.makeAccount(newUser);
//		assertNotNull(user);
//		try {
//		Settings settings = settingsRepo.findById(user.getSettings().getId()).get();
//		assertNotNull(settings);
//		} catch (NoSuchElementException e) {
//			e.printStackTrace();
//			throw new NoSuchSettingsOptionException();
//		}
//	}

//	@Test
//	public void getUser() throws NotExistingUserException {
//		User user = this.userService.getExistingUserById(new Long(9));
//		assertNotNull(user);
//	}
	
//	@Test
//	public void login() throws WrongPasswordException, NotExistingUserException {
//		LoginDTO logger = new LoginDTO("Mirkata11@abv.bg","Cska1948#");
//		User us = this.userService.login(logger);
//		assertNotNull(us);
//		assertNotNull(us.getSettings());
//	}
	
//	@Test
//	public void viewProfile() {
//		UserDTO profile = this.userService.getUserProfile(new Long(9));
//		assertNotNull(profile);
//	}
	
//	@Test
//	public void updateProfile() throws NotExistingUserException, DateFormatException, NoSuchSettingsOptionException {
//		Long userId = new Long(9);
//		User user = this.userService.getExistingUserById(userId);
//
//		UpdateProfileDTO update = new UpdateProfileDTO();
//		update.setUsername("TUPOOOOOO12");
//
//		this.userService.updateProfile(userId, update);
//		user = this.userService.getExistingUserById(userId);
//
//		assertEquals(update.getUsername(),user.getUsername());
//	}
//	
	
//	@Test
//	public void softDeleteUser() throws NotExistingUserException {
//		User user = this.userService.getExistingUserById(new Long(7));
//		this.userService.softDeleteUser(new Long(7));
//		try {
//		user = this.repo.findById(new Long(7)).get();
//		} catch(NoSuchElementException e) {
//			user = null;
//		}
//		assertTrue(user == null);
//	}
	
//	@Test
//	public void retrieveUser() throws WrongPasswordException, SQLException, NotExistingUserException {
//		LoginDTO lazarus = new LoginDTO("Mirkata11@abv.bg","Cska1948#");
//		User lazar = this.userService.retrieveUser(lazarus);
//		assertNotNull(lazar);
//	}
	
	
	
	
}
