package com.financeManager.demo.tests;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.financeManager.demo.dto.IUsersRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddUserTest {
	
	@Autowired
	private IUsersRepository repo;

//	@Test
//	public void addUser() {
//		repo.save(
//				new User(null,"user123","parola12","nqma123"));
//		
//	}

}
