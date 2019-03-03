package com.financeManager.demo.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.dto.UpdateProfileDTO;
import com.financeManager.demo.dto.UserDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
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
	
	@Autowired
	SettingsService settingsService; 
	
	public User makeAccount(CreateUserDTO newUser) {	
		User usi = new User(null, newUser.getEmail(), newUser.getPassword(), newUser.getUsername(), null,(byte)1);
		userRepo.save(usi);	
		Settings userSettings = new Settings(usi.getId(), usi);
		settingsRepo.save(userSettings);
		usi.setSettings(userSettings);
		return usi;
	}
	
	public User getExistingUserById(Long id) throws NotExistingUserException {
		try {
			User usi = this.userRepo.findById(id).get();
			 return usi;
		} catch (NoSuchElementException e){
			throw new NotExistingUserException();
		}
	}
	
	public User login(LoginDTO logger) throws WrongPasswordException, NotExistingUserException {
		User us = null;
		try {
			us = this.userRepo.findByEmail(logger.getEmail()).get();
		}catch(NoSuchElementException e) {
			throw new NotExistingUserException();
		}
		
		
		if(!us.getPassword().equals(logger.getPassword())){
			throw new WrongPasswordException();
		} 
		
		Settings settings = this.settingsRepo.findById(us.getId()).get();
		us.setSettings(settings);
		
		return us;
	
		
	}
	
	public User getExistingUserByEmail(String email) throws NotExistingUserException {
		try {
			User usi = this.userRepo.findByEmail(email).get();
			 return usi;
		} catch (NoSuchElementException e){
			throw new NotExistingUserException();
		}
	
	}
	
	
	
	public UserDTO getUserProfile(User us) {
		
		UserDTO profile = new UserDTO();
		profile.setEmail(us.getEmail());
		profile.setUsername(us.getUsername());	
		profile.setSettings(settingsService.getSettings(us.getSettings().getId()));
		
		return profile;
	}

	public void updateProfile(User usi, UpdateProfileDTO updates) throws DateFormatException, NoSuchSettingsOptionException {
		
		if(updates.getUsername() != null) {
			usi.setUsername(updates.getUsername());
		}
		
		if(updates.getPassword() != null) {
			usi.setPassword(updates.getPassword());
		}
		
		if(updates.getSettings() != null) {
			Settings newSettings = settingsService.update(usi.getSettings(), updates.getSettings());
			usi.setSettings(newSettings);
		}

		userRepo.save(usi);
	}
	
	public void softDeleteUser(Long id) throws NotExistingUserException {
		User user = this.getExistingUserById(id);	
		user.setIsDeleted((byte)1);
		this.userRepo.save(user);
		
	}
		
	
}
