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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private Double balance;
	@Column(name = "max_limit")
	private Double limit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

//	@OneToMany(mappedBy = "transactions")
////	@JoinColumn(name = "wallet_id")
//	private Set<Transaction> transactions;

	public Wallet(String name, Double balance, Double limit, User owner) {
		this.name = name;
		if (balance != null) {
			this.balance = balance;
		} else {
			this.balance = 0.0;
		}

		if (limit != null) {
			this.limit = limit;
		} else {
			this.limit = 0.0;
		}
		this.user = owner;
	}

}
