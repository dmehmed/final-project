package com.financeManager.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayActivityDTO {

	private String day;
	private long transactionCount;
	private long incomeCount;
	private Double incomeSum;
	private long expenseCount;
	private Double expenseSum;
	private List<TransactionDTO> transactions;

}
