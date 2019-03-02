package com.financeManager.demo.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Settings {

	@Id
	@Column(name = "user_id")
	private Long id;
	
	@OneToOne
    @MapsId
    private User user;
	private Date birthdate;

	@ManyToOne
	@Autowired
	private Country country;
	
	@ManyToOne
	@Autowired
	private Currency currency;

	@ManyToOne
	@Autowired
	private Gender gender;
	
	public Settings(Long id, User user) {
		this.id = id;
		this.user = user;
	}
	
}
