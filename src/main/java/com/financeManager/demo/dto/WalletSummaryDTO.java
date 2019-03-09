package com.financeManager.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletSummaryDTO {
	
	private Long id;
	private String name;
	private int transactionCount;
	private double totalMoneyReceived;
	private double totalMoneyPayed;
	private double balance;
}
