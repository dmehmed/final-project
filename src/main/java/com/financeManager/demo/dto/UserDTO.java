package com.financeManager.demo.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	
	private String email;
	private String username;
	private String country;
	private String gender;
	private Date birthdate;
	private String currency;

}
