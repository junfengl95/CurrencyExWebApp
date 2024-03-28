package com.fdm.velocitytrade.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "BANK")
public class Bank {

	// when read is null until published to DB
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bankAccountId;
	private String bankName;
	private String accountNumber;
	private String currencyname;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bank")
	@JsonBackReference(value = "user-bank")
	private User user;

	public Bank(String bankName, String accountNumber, String currencyname) {

		super();
		this.currencyname = currencyname;
		this.bankName = bankName;
		this.accountNumber = accountNumber;
	}

	public String getCurrencyname() {

		return currencyname;

	}

	public void setCurrencyname(String currencyname) {

		this.currencyname = currencyname;

	}

	public String getBankName() {

		return bankName;

	}

	public void setBankName(String bankName) {

		this.bankName = bankName;

	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Bank() {

		super();

	}

	public Long getBankAccountId() {

		return bankAccountId;

	}

	public void setBankAccountId(Long bankAccountId) {

		this.bankAccountId = bankAccountId;

	}

	public User getUser() {

		return user;

	}

	public void setUser(User user) {

		this.user = user;

	}

}
