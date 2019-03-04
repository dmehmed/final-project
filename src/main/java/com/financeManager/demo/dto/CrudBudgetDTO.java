package com.financeManager.demo.dto;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrudBudgetDTO {
	
	@Min(value = 0)
	private Double amount;
	private Long categoryId;
	private Long repeatPeriodId;
	
}
