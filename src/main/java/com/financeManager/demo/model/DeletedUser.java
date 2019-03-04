package com.financeManager.demo.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = 1")
public class DeletedUser{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	private String email;

	private String password;

	private String username;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private Settings settings;
	
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Wallet> wallets;

	private byte isDeleted;



}