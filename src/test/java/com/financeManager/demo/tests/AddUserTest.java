package com.financeManager.demo.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddUserTest {
	
	@Autowired
	private UserService service;

	CreateUserDTO userDto = new CreateUserDTO("ivanmirchev2342@abv.bg", "parolaMarol22221#","nekuvUsername");
	
//	@Test
//	public void addUser() {
//		
//		this.service.makeAccount(userDto);
//	}

}
