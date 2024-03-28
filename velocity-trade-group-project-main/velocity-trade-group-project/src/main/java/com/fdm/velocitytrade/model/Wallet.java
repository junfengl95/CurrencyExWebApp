package com.fdm.velocitytrade.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "WALLET")
public class Wallet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long walletId;
	
	
//	
	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "wallet_currency", joinColumns = @JoinColumn(name = "wallet_id"))
    @MapKeyColumn(name = "currency_name")
    @Column(name = "amount")
	private Map<String, Double> currencyMap; 

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userId", referencedColumnName = "userId")
	@JsonManagedReference(value = "user-wallet")
	private User user;

	public Wallet() {
		super();
	}

	public Wallet(Map<String, Double>currencyMap) {
		super();
		this.currencyMap = currencyMap;

	}
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {

		return " wallet username : " + user.getUsername();
	}



	public long getWalletId() {
		return walletId;
	}

	public void setWalletId(long walletId) {
		this.walletId = walletId;
	}

	public Map<String, Double> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, Double> currencyMap) {
		this.currencyMap = currencyMap;
	}

}
