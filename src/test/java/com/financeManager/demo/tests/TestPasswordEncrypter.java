package com.financeManager.demo.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPasswordEncrypter {

	@Autowired 
	private UserService service;
	
	private String password = "parolaMarola23#";
	
	@Test()
	public void testPassowrdEncrypter() {
		
//		String encrypted = this.service.hashPassword(password);
//		
//		
//			this.service.checkPass(password, encrypted);
//			assertTrue(password.equals(encrypted));

	}
	
	
}
