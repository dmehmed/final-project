package com.financeManager.demo.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.financeManager.demo.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrudWalletDTO {

	@NotNull
	private String name;
	@Min(value = 0)
	private double balance;
	@Min(value = 50)
	private double limit;

}
