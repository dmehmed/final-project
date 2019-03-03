package com.financeManager.demo.services;

import java.util.NoSuchElementException;

import org.apache.commons.codec.digest.DigestUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.dto.UpdateProfileDTO;
import com.financeManager.demo.dto.UserDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.WrongPasswordException;
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
		User usi = new User(newUser.getEmail(), DigestUtils.sha256Hex(newUser.getPassword()), newUser.getUsername());
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
		
		String decrypted = DigestUtils.sha256Hex(logger.getPassword());
		if(!us.getPassword().equals(decrypted)){
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
	
	
	
	public UserDTO getUserProfile(Long id) {
		
		UserDTO profile = new UserDTO();
		
		User user = this.userRepo.findById(id).get();
		
		profile.setEmail(user.getEmail());
		profile.setUsername(user.getUsername());	
		profile.setSettings(settingsService.getSettings(user.getSettings().getId()));
		

		return profile;
	}

	public void updateProfile(Long id, UpdateProfileDTO updates) throws DateFormatException, NoSuchSettingsOptionException {
		
		if(updates.getUsername() != null) {
			 this.userRepo.findById(id).get().setUsername(updates.getUsername());
		}
		
		if(updates.getPassword() != null) {
			 this.userRepo.findById(id).get().setPassword(DigestUtils.sha256Hex(updates.getPassword()));
		}
		
		if(updates.getSettings() != null) {
			Settings newSettings = settingsService.update(id, updates.getSettings());
			 this.userRepo.findById(id).get().setSettings(newSettings);
		}

		this.userRepo.save(this.userRepo.findById(id).get());

	}
	
	public void softDeleteUser(Long id) throws NotExistingUserException {
		User user = this.getExistingUserById(id);	
		user.setIsDeleted((byte)1);
		this.userRepo.save(user);
		
	}

	
	public boolean hasUserWithEmail(String email) {
		return this.userRepo.findByEmail(email).isPresent();
	}

	
}
