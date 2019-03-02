package com.financeManager.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financeManager.demo.dao.ICountryDAO;
import com.financeManager.demo.dao.ICurrencyDAO;
import com.financeManager.demo.dao.IGenderDAO;
import com.financeManager.demo.dto.SettingsDTO;
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
}
