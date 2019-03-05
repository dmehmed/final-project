package com.financeManager.demo.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Budget {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Double amount;
	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "end_date")
	private Date endDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name = "repeat_period_id")
	private RepeatPeriod repeatPeriod;
	
	public Budget(Double amount, Date startDate, Date endDate, User owner, Category category, RepeatPeriod repeatPeriod) {
		this.amount = amount;
		this.startDate = startDate;
		this.endDate = endDate;
		this.user = owner;
		this.category = category;
		this.repeatPeriod = repeatPeriod;
	}
}
