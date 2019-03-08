package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PercentageOfCategoryAmountDTO {

	private String category;
	private Double amount;
	private int transactionCount;
	private String percentageOfTotal;
	
}
