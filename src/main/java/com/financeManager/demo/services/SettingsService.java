package com.financeManager.demo.services;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.ICountryDAO;
import com.financeManager.demo.dao.ICurrencyDAO;
import com.financeManager.demo.dao.IGenderDAO;
import com.financeManager.demo.dto.SettingsDTO;
import com.financeManager.demo.dto.SettingsUpdateDTO;
import com.financeManager.demo.exceptions.DateFormatException;
import com.financeManager.demo.exceptions.NoSuchSettingsOptionException;
import com.financeManager.demo.model.Settings;
import com.financeManager.demo.repositories.ISettingsRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsService {

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	@Autowired
	private ISettingsRepository settingsRepo;
	@Autowired
	private ICountryDAO countryDAO;

	@Autowired
	private ICurrencyDAO currencyDAO;

	@Autowired
	private IGenderDAO genderDAO;

	public SettingsDTO getSettings(Long id) {

		SettingsDTO view = new SettingsDTO();

		Settings settings = settingsRepo.findById(id).get();
		setFields(view, settings);

		return view;
	}

	private boolean isOptionalFieldEmpty(Object object) {
		return object == null ? true : false;
	}

	private void setFields(SettingsDTO view, Settings settings) {

		view.setBirthdate(isOptionalFieldEmpty(settings.getBirthdate()) ? "" : settings.getBirthdate().toString());
		view.setCountry(isOptionalFieldEmpty(settings.getCountry()) ? "" : settings.getCountry().getName());
		view.setCurrency(isOptionalFieldEmpty(settings.getCurrency()) ? "" : settings.getCurrency().getType());
		view.setGender(isOptionalFieldEmpty(settings.getGender()) ? "" : settings.getGender().getName());
		
	}

	public Settings update(Long id, SettingsUpdateDTO update) throws DateFormatException, NoSuchSettingsOptionException {
		
		if(update.getBirthdate() != null) {
			try {
				java.util.Date newBirthdate = new SimpleDateFormat(DATE_FORMAT).parse(update.getBirthdate());
				java.sql.Date sqlBirthday = new java.sql.Date(newBirthdate.getTime());
				this.settingsRepo.findById(id).get().setBirthdate(sqlBirthday);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new DateFormatException("Wrong date format", e);
			}
		}
		
		try {
		
		if(update.getCountryId() != null) {
			this.settingsRepo.findById(id).get().setCountry(countryDAO.getById(update.getCountryId()));
		}
		
		if(update.getCurrencyId() != null) {
			this.settingsRepo.findById(id).get().setCurrency(currencyDAO.getById(update.getCurrencyId()));
		}
		
		if(update.getGenderId() != null) {
			this.settingsRepo.findById(id).get().setGender(genderDAO.getById(update.getGenderId()));
		}
		
		} catch (NoSuchElementException e) {
			throw new NoSuchSettingsOptionException("There is no such option", e);
		}
		
		settingsRepo.save(this.settingsRepo.findById(id).get());
		return this.settingsRepo.findById(id).get();
		
	}
}
