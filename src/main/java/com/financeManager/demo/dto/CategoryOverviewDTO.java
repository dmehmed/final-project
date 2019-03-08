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
public class CategoryOverviewDTO {

	private String from;
	private String till;
	private List<PercentageOfCategoryAmountDTO> categoriesOverview;
	
}
