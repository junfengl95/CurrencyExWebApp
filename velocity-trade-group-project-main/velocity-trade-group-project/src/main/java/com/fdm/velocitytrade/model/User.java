package com.fdm.velocitytrade.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 */
@Entity
@Table(name = "USER")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	private String username;
	private String password;
	private String email;
	private String role;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
	@JsonBackReference(value = "user-wallet")
	private Wallet wallet;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bankAccountId")
	@JsonManagedReference(value = "user-bank")
	private Bank bank;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "tradeId")
	@JsonManagedReference(value = "user-trade")
	private List<Trade> tradeIdList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "transactionHistoryId")
	@JsonManagedReference(value = "user-transactionhistory")
	private List<TransactionHistory> transactionHistoryIdList;

	public User(String username, String password, String email, String role, Wallet wallet, Bank bank) {

		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.wallet = wallet;
		this.wallet.setUser(this);
		this.bank = bank;
		this.bank.setUser(this);

	}

	public User() {

		super();

	}

	public Long getUserId() {

		return userId;

	}

	public void setUserId(Long userId) {

		this.userId = userId;

	}

	public String getUsername() {

		return username;

	}

	public void setUsername(String username) {

		this.username = username;

	}

	public String getEmail() {

		return email;

	}

	public void setEmail(String email) {

		this.email = email;

	}

	public String getRole() {

		return role;

	}

	public void setRole(String role) {

		this.role = role;

	}

	public Wallet getWallet() {

		return wallet;

	}

	public void setWallet(Wallet wallet) {

		this.wallet = wallet;

	}

	public Bank getBank() {

		return bank;

	}

	public void setBank(Bank bank) {

		this.bank = bank;

	}

	public String getPassword() {

		return password;

	}

	public void setPassword(String password) {

		this.password = password;

	}

	public List<Trade> getTradeIdList() {

		return tradeIdList;

	}

	public void setTradeIdList(List<Trade> tradeIdList) {

		this.tradeIdList = tradeIdList;

	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", role=" + role + ", wallet=" + wallet + ", bank=" + bank + ", tradeIdList=" + tradeIdList
				+ ", transactionHistoryIdList=" + transactionHistoryIdList + "]";
	}



}
