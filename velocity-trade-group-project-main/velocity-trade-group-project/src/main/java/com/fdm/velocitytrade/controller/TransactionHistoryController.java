package com.fdm.velocitytrade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.service.TransactionHistoryService;

@RestController
@RequestMapping("/TransactionHistory")
public class TransactionHistoryController {

	@Autowired
	private TransactionHistoryService transactionHistoryService;

	@GetMapping("/{userId}")
	public List<TransactionHistory> retrieveAllTransactionHistoryForUserID(@PathVariable Long userId) {
		List<TransactionHistory> transactionHistoryList = transactionHistoryService.findAllByUserId(userId);
		return transactionHistoryList;
	}
}
