package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetOverviewDTO {

	private Long budgetId;
	private int transactionCount;
	private String periodOfBudget;
	private String categoryName;
	private Double budgetAmount;
	private Double moneySpent;
	private Double finalCalculation;
	private String status;
}
