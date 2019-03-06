package com.financeManager.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

	private String transactionType;
	private String categoryType;
	private String walletName;
	private Double amount;
	private LocalDateTime creationDate;
	
}
