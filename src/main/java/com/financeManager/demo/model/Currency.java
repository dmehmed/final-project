package com.financeManager.demo.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

	@Id
	private Long id;
	private String type;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "currency_id")
	private Set<Settings> settings;

}