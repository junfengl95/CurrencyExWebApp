package com.fdm.velocitytrade.model;


import java.time.LocalDateTime;

//import java.lang.reflect.Field;
//import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fdm.velocitytrade.ORDERTYPE;
//import com.fdm.velocitytrade.TRADESTATUS;
//import com.fdm.velocitytrade.TRADETYPE;
import com.fdm.velocitytrade.TRADESTATUS;

//import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TRADE")
public class Trade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tradeId;
	private String currencyFrom;
	private String currencyTo;

	// limit, spot, forward
	@Enumerated(EnumType.STRING)
	private ORDERTYPE orderType;

	private double price;
	private double amountFrom;
	private double amountTo;
	private String expiryDate;
	private LocalDateTime executionDate;
	private String forwardDate;
	
	@Enumerated(EnumType.STRING)
	private TRADESTATUS tradeStatus;

	@ManyToOne()
	@JoinColumn(name = "userId")
	@JsonBackReference(value = "user-trade")
	private User user;

	public Trade(String currencyFrom, String currencyTo, ORDERTYPE orderType, double price, double amountFrom,
//			double amountTo, String expiryDate, LocalDateTime executionDate, String forwardDate, User user) {
		double amountTo, String expiryDate, String forwardDate, User user) {

		super();
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
		this.orderType = orderType;
		this.price = price;
		this.amountFrom = amountFrom;
		this.amountTo = amountTo;
		this.expiryDate = expiryDate;
		this.forwardDate = forwardDate;
		this.user = user;
		
		// executionDate and tradeStatus will be created on backend upon creation of trade from API call. will be passed in from frontEnd
		this.executionDate = LocalDateTime.now();
		this.tradeStatus = TRADESTATUS.pending;

	}

	public Trade() {

		// TODO Auto-generated constructor stub
	}

	public double getPrice() {

		return price;

	}

	public void setPrice(double price) {

		this.price = price;

	}

	public double getAmountFrom() {

		return amountFrom;

	}

	public void setAmountFrom(double amountFrom) {

		this.amountFrom = amountFrom;

	}

	public double getAmountTo() {

		return amountTo;

	}

	public void setAmountTo(double amountTo) {

		this.amountTo = amountTo;

	}

	public long getTradeId() {

		return tradeId;

	}

	public void setTradeId(long tradeId) {

		this.tradeId = tradeId;

	}

	public String getCurrencyFrom() {

		return currencyFrom;

	}

	public void setCurrencyFrom(String currencyFrom) {

		this.currencyFrom = currencyFrom;

	}

	public String getCurrencyTo() {

		return currencyTo;

	}

	public void setCurrencyTo(String currencyTo) {

		this.currencyTo = currencyTo;

	}

	public String getExpiryDate() {

		return expiryDate;

	}

	public void setExpiryDate(String expiryDate) {

		this.expiryDate = expiryDate;

	}

	public ORDERTYPE getOrderType() {

		return orderType;

	}

	public void setOrderType(ORDERTYPE orderType) {

		this.orderType = orderType;

	}

	public User getUser() {

		return user;

	}

	public void setUser(User user) {

		this.user = user;

	}

	public LocalDateTime getExecutionDate() {

		return executionDate;

	}

	public void setExecutionDate(LocalDateTime executionDate) {

		this.executionDate = executionDate;

	}

	public String getForwardDate() {
		return forwardDate;
	}

	public void setForwardDate(String forwardDate) {
		this.forwardDate = forwardDate;
	}

	
	
	
	public TRADESTATUS getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TRADESTATUS tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	@Override
	public String toString() {
		return "Trade id : " + tradeId + " User who made trade : " + user.getUsername();
	}

	public Trade update(Trade obj) {

		this.orderType = (obj.orderType.toString().equals(null)) ? this.orderType : obj.orderType;

		this.price = obj.price;
		this.amountFrom = obj.amountFrom;
		this.amountTo = obj.amountTo;
		this.currencyFrom = obj.currencyFrom;
		this.currencyTo = obj.currencyTo;
		this.expiryDate = obj.expiryDate;
		this.forwardDate = obj.forwardDate;
		this.tradeStatus = obj.tradeStatus;
		this.executionDate = obj.executionDate;

		return this;

	}

	// This method will copy the source onto target (this), however
	// reference types are still the same
	public Trade deepCopy(Trade sourceTrade) {

		// Assigning primitive and immutable fields
		this.tradeId = sourceTrade.getTradeId();
		this.currencyFrom = sourceTrade.getCurrencyFrom();
		this.currencyTo = sourceTrade.getCurrencyTo();
		this.orderType = sourceTrade.getOrderType();
		this.price = sourceTrade.getPrice();
		this.amountFrom = sourceTrade.getAmountFrom();
		this.amountTo = sourceTrade.getAmountTo();
		this.expiryDate = sourceTrade.getExpiryDate();
		this.executionDate = sourceTrade.getExecutionDate();
		this.forwardDate = sourceTrade.getForwardDate();
		this.tradeStatus = sourceTrade.getTradeStatus();
		// For reference type like user, it will be a shallow copy
		this.user = sourceTrade.getUser();
		
		return this;
	}

	public boolean isEmpty() {

		
		if (tradeId != 0 && amountFrom != 0 && amountTo != 0)
			
			return false;

		return true;
	}

}
