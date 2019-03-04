package com.financeManager.demo.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {

	private double amount;
	private Date startDate;
	private Date endDate;
	private String category;
	private String repeatPeriod;
	
}
