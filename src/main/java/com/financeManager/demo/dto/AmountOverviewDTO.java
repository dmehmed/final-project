package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmountOverviewDTO {

	private String from;
	private String till;
	private Double incomes;
	private Double expenses;
	private Double savings;
	private Double overruns;
	
}
