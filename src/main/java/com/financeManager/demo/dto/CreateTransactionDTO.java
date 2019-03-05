package com.financeManager.demo.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionDTO {

	@Min(value = 1)
	private Double amount;
	
	private String description;
	
	@NotNull
	private Long walletId;
	
	@NotNull
	private Long categoryId;
	
}
