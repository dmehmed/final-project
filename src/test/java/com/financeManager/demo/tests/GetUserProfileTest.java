package com.financeManager.demo.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.model.User;
import com.financeManager.demo.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetUserProfileTest {

	@Autowired
	private UserService service;
	Long id = new Long((long) 30);
	@Test
	public void testUserProfile() throws NotExistingUserException {
		User us = this.service.getExistingUserById(id);
	this.service.getUserProfile(us.getId());
	}
}
