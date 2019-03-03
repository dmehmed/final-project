package com.financeManager.demo.model;

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
	@Column(name = "max_limit")
	private double limit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
//	@OneToMany(mappedBy = "transactions")
////	@JoinColumn(name = "wallet_id")
//	private Set<Transaction> transactions;
	
	public Wallet(String name, double balance, double limit,User owner) {
		this.name = name;
		this.balance = balance;
		this.limit = limit;
		this.user = owner;
		System.out.println(owner.getId());
	}
	
//	public Wallet(String name, double balance, double limit,User owner) {
//		this.name = name;
//		this.balance = balance;
//		this.limit = limit;
//		this.user = owner;
//		System.out.println(owner.getId());
//	}
	
}
