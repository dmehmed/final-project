package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDTO {

	private String birthdate;
	private String gender;
	private String country;
	private String currency;

}
