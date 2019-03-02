package com.financeManager.demo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserDTO {	
	@NotNull
	@Email
	private String email;
	@NotNull
	private String password;
	@NotNull
	private String username;
}
