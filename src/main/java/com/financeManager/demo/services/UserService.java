package com.financeManager.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.repositories.ISettingsRepository;
import com.financeManager.demo.repositories.IUsersRepository;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.model.Settings;
import com.financeManager.demo.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserService {
	
	@Autowired
	private IUsersRepository userRepo;
	@Autowired
	private ISettingsRepository settingsRepo;
	
	public User makeAccount(CreateUserDTO newUser) {	
		User usi = new User(null, newUser.getEmail(), newUser.getPassword(), newUser.getUsername(), null);
		userRepo.save(usi);	
		Settings userSettings = new Settings(usi.getId(), usi);
		settingsRepo.save(userSettings);
		usi.setSettings(userSettings);
		return usi;
	}
	
	public User getExistingUserById(Long id) throws NotExistingUserException {
		User usi = this.userRepo.findById(id).get();
		if(usi == null) {
			throw new NotExistingUserException();
		}
		return usi;
	}
	
	public User getExistingUserByEmail(String email) throws NotExistingUserException {
		User usi = this.userRepo.findByEmail(email);
		if(usi == null) {
			throw new NotExistingUserException();
		}
		return usi;
	}
	
	
	
	
}
