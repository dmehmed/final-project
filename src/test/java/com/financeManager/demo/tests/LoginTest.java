package com.financeManager.demo.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.WrongPasswordException;
import com.financeManager.demo.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginTest {
	
	@Autowired
	private UserService service;
	
	LoginDTO logger = new LoginDTO(null, "parolaMarol1#");
	
	@Test
	public void loginUser() throws WrongPasswordException, NotExistingUserException {
		this.service.login(logger);
	}

}
