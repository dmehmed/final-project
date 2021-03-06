package com.financeManager.demo.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Settings {

	@Id
	@Column(name = "user_id")
	private Long id;

	@OneToOne
	@MapsId
	private User user;
	private Date birthdate;
	@ManyToOne(fetch = FetchType.EAGER)
	private Country country;
	@ManyToOne(fetch = FetchType.EAGER)
	private Currency currency;
	@ManyToOne(fetch = FetchType.EAGER)
	private Gender gender;

	public Settings(Long id, User user) {
		this.id = id;
		this.user = user;
		this.currency = new Currency();
		this.currency.setId((long)1); 
	}

}
