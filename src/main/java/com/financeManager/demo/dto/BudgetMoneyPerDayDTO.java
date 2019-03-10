package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetMoneyPerDayDTO {

	private String category;
	private Double remainingAmount;
	private Double amountPerDay;
	
}
