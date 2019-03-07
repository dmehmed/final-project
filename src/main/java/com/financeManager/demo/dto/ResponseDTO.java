package com.financeManager.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

	private Timestamp timeStamp;
	private	int status;
	private String message;
	private Long id;
	
}
