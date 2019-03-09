package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BestAndWorseMonthOverviewDTO {

	private String bestMonth;
	private Double monthIncome;
	private String worstMonth;
	private Double monthExpense;
	
}
