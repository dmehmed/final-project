package com.financeManager.demo.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "repeat_periods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RepeatPeriod {

	@Id
	private Long id;
	private String period;
	
	@OneToMany(mappedBy = "repeatPeriod", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Budget> budgets;
	
}
