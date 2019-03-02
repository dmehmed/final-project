package com.financeManager.demo.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)

	private Long id;

	private String email;

	private String password;

	private String username;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private Settings settings;
	
//	@Column(name = "isDeleted")
	private byte isDeleted;


		
}
