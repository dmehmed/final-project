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

	private Long id;
	private String transactionType;
	private Double amount;
	private LocalDateTime creationDate;
	private String categoryType;
	private String walletName;

}
