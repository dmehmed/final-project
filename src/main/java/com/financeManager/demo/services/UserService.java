package com.financeManager.demo.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dto.CreateUserDTO;
import com.financeManager.demo.dto.LoginDTO;
import com.financeManager.demo.dto.UpdateProfileDTO;
import com.financeManager.demo.dto.UserDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.exceptions.NotExistingUserException;
import com.financeManager.demo.exceptions.UserWithThisEmailAlreadyExistsException;
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
	private SettingsService settingsService;
	@Autowired
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();

	public User makeAccount(CreateUserDTO newUser) throws SQLException, UserWithThisEmailAlreadyExistsException {
		Connection con = jdbcTemplate.getDataSource().getConnection();
		ResultSet rs = con.createStatement()
				.executeQuery("SELECT * FROM users where email = '" + newUser.getEmail() + "'");

		if (rs.next()) {
			throw new UserWithThisEmailAlreadyExistsException("User with this email already exists!");
		}

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
		} catch (NoSuchElementException e) {
			throw new NotExistingUserException("User doesn't exists!");
		}
	}

	public User login(LoginDTO logger) throws WrongPasswordException, NotExistingUserException {
		User us = null;
		try {
			us = this.userRepo.findByEmail(logger.getEmail()).get();
		} catch (NoSuchElementException e) {
			throw new NotExistingUserException("User doesn't exists!");
		}

		String decrypted = DigestUtils.sha256Hex(logger.getPassword());
		if (!us.getPassword().equals(decrypted)) {
			throw new WrongPasswordException("Wrong password!");
		}

		Settings settings = this.settingsRepo.findById(us.getId()).get();
		us.setSettings(settings);

		return us;

	}

	public User getExistingUserByEmail(String email) throws NotExistingUserException {
		try {
			User usi = this.userRepo.findByEmail(email).get();
			return usi;
		} catch (NoSuchElementException e) {
			throw new NotExistingUserException("User doesn't exists!");
		}

	}

	public UserDTO getUserProfile(Long id) {

		UserDTO profile = new UserDTO();

		User user = this.userRepo.findById(id).get();

		profile.setId(user.getId());
		profile.setEmail(user.getEmail());
		profile.setUsername(user.getUsername());
		profile.setSettings(settingsService.getSettings(user.getSettings().getId()));

		return profile;
	}

	public void updateProfile(Long id, UpdateProfileDTO updates)
			throws DateFormatException, NoSuchSettingsOptionException {	
		
		User user = this.userRepo.findById(id).get();
		
		if (updates.getUsername() != null) {
			user.setUsername(updates.getUsername());
		}
		System.out.println(updates.getUsername());
		
		if (updates.getPassword() != null) {
			user.setPassword(DigestUtils.sha256Hex(updates.getPassword()));
		}

		if (updates.getSettings() != null) {
			Settings newSettings = settingsService.update(id, updates.getSettings());
			user.setSettings(newSettings);
		}
		
		this.userRepo.saveAndFlush(user);
		

	}

	public void softDeleteUser(Long id) throws NotExistingUserException {
		User user = this.getExistingUserById(id);
		user.setIsDeleted((byte) 1);
		this.userRepo.saveAndFlush(user);
	}

	public boolean hasUserWithEmail(String email) {
		return this.userRepo.findByEmail(email).isPresent();
	}

	public User retrieveUser(LoginDTO lazarus) throws WrongPasswordException, SQLException, NotExistingUserException {

		Connection con = jdbcTemplate.getDataSource().getConnection();
		ResultSet rs = con.createStatement()
				.executeQuery("SELECT * FROM users where email = '" + lazarus.getEmail() + "' and is_deleted = 1");

		if (rs.next()) {
			ResultSet st = con.createStatement()
					.executeQuery("SELECT password FROM users where email = '" + lazarus.getEmail() + "'");
			st.next();
			String pass = st.getString(1);
			if (pass.equals(DigestUtils.sha256Hex(lazarus.getPassword()))) {
				con.createStatement()
						.executeUpdate("update users set is_deleted = 0 where email = '" + lazarus.getEmail() + "'");
			} else {
				throw new WrongPasswordException("Wrong password!");
			}
		} else {
			throw new NotExistingUserException("User doesn't exists!");
		}

		return this.userRepo.findByEmail(lazarus.getEmail()).get();
	}

	public void saveUserInRepo(User user) {
		this.userRepo.save(user);
	}

}
