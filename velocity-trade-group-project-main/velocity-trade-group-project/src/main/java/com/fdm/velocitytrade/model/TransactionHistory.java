package com.fdm.velocitytrade.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fdm.velocitytrade.ORDERTYPE;
import com.fdm.velocitytrade.TRADESTATUS;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TRANSACTION_HISTORY")
public class TransactionHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long transactionHistoryId;

	private String currencyFrom;
	private String currencyTo;
	// limit, spot, forward
	private ORDERTYPE orderType;
	private double price;
	private double amountFrom;
	private double amountTo;
	private String expiryDate;
	private LocalDateTime executionDate;
	private long matchedTradeId;
	private TRADESTATUS status;
	private long tradeId;
	private String forwardDate;

	@ManyToOne()
	@JoinColumn(name = "userId")
	@JsonBackReference(value = "user-transactionhistory")
	private User user;

	public TransactionHistory(Trade trade, TRADESTATUS status, long matchedTradeId) {

		super();
		this.user = trade.getUser();
		this.currencyFrom = trade.getCurrencyFrom();
		this.currencyTo = trade.getCurrencyTo();
		this.orderType = trade.getOrderType();
		this.price = trade.getPrice();
		this.amountFrom = trade.getAmountFrom();
		this.amountTo = trade.getAmountTo();
		this.expiryDate = trade.getExpiryDate();
		this.executionDate = trade.getExecutionDate();
		this.matchedTradeId = matchedTradeId;
		this.status = status;
		this.tradeId = trade.getTradeId();
		this.forwardDate = trade.getForwardDate();

	}

	public TransactionHistory(Trade trade, TRADESTATUS status) {

		super();
		this.user = trade.getUser();
		this.currencyFrom = trade.getCurrencyFrom();
		this.currencyTo = trade.getCurrencyTo();
		this.orderType = trade.getOrderType();
		this.price = trade.getPrice();
		this.amountFrom = trade.getAmountFrom();
		this.amountTo = trade.getAmountTo();
		this.expiryDate = trade.getExpiryDate();
		this.executionDate = trade.getExecutionDate();
		this.status = status;
		this.tradeId = trade.getTradeId();
		this.forwardDate = trade.getForwardDate();

	}

	public TransactionHistory() {

		// TODO Auto-generated constructor stub
	}

	public User getUser() {

		return user;

	}

	public void setUser(User user) {

		this.user = user;

	}

	public ORDERTYPE getOrderType() {

		return orderType;

	}

	public void setOrderType(ORDERTYPE orderType) {

		this.orderType = orderType;

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

	public TRADESTATUS getStatus() {

		return status;

	}

	public void setStatus(TRADESTATUS status) {

		this.status = status;

	}

	public long getMatchedTradeId() {

		return matchedTradeId;

	}

	public void setMatchedTradeId(long matchedTradeId) {

		this.matchedTradeId = matchedTradeId;

	}

	public long getTransactionHistoryId() {
		return transactionHistoryId;
	}

	public void setTransactionHistoryId(long transactionHistoryId) {
		this.transactionHistoryId = transactionHistoryId;
	}

	public LocalDateTime getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(LocalDateTime executionDate) {
		this.executionDate = executionDate;
	}

	public long getTradeId() {
		return tradeId;
	}

	public void setTradeId(long tradeId) {
		this.tradeId = tradeId;
	}

	public String getForwardDate() {
		return forwardDate;
	}

	public void setForwardDate(String forwardDate) {
		this.forwardDate = forwardDate;
	}

	
	
}
