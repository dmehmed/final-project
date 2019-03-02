package com.financeManager.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.dto.UserDTO;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.WrongPasswordException;
import com.financeManager.demo.model.Country;
import com.financeManager.demo.model.Settings;
import com.financeManager.demo.model.User;
import com.financeManager.demo.repositories.ISettingsRepository;
import com.financeManager.demo.repositories.IUsersRepository;

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
		
		System.out.println("Method get " + usi.getSettings());
		
		return usi;
	}
	
	public User login(LoginDTO logger) throws WrongPasswordException {
		
		User us = this.userRepo.findByEmail(logger.getEmail());
		
		if(!us.getPassword().equals(logger.getPassword())){
			throw new WrongPasswordException();
		} 
		
		Settings settings = this.settingsRepo.findById(us.getId()).get();
		settings.setCountry(new Country(null,"Bulgaria"));
		us.setSettings(settings);
		System.out.println(us.getSettings().getCountry().getName());
		System.out.println(us.getId());
		
		return us;
		
	}
	
	public User getExistingUserByEmail(String email) throws NotExistingUserException {
		User usi = this.userRepo.findByEmail(email);
		if(usi == null) {
			throw new NotExistingUserException();
		}
		return usi;
	}
	
	
	
	public UserDTO getUserProfile(User us) {
		
		UserDTO profile = new UserDTO();
//		profile.setCurrency(us.getSettings().getCurrency().getType());
//		profile.setCountry(us.getSettings().getCountry().getName());
//		profile.setBirthdate(us.getSettings().getBirthdate());
		profile.setEmail(us.getEmail());
		profile.setUsername(us.getUsername());
//		profile.setGender(us.getSettings().getGender().getName());
		return profile;
	}
	
	
	
}
