package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsUpdateDTO {

	private String birthdate;
	private Long countryId;
	private Long currencyId;
	private Long genderId;
}
