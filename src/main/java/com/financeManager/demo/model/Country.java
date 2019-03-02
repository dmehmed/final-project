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
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "countries")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Country {

	@Id
	private final Long id;
	private final String name;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "country_id")
	private Set<Settings> settings;

}
