package com.financeManager.demo.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrudWalletDTO {

	@Size(min = 5)
	private String name;
	@Min(value = 0)
	private Double balance;
	@Min(value = 0)
	private Double limit;

}
