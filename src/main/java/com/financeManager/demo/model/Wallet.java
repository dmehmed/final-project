package com.financeManager.demo.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private double balance;
	
	private double limit;
	
	@ManyToOne
	private User user;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "wallet_id")
	private Set<Transaction> transactions;
	
}
