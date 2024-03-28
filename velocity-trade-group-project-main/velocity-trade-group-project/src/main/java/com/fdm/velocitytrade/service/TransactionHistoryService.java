package com.fdm.velocitytrade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;

@Service
public class TransactionHistoryService {
	
    @Autowired
    TransactionHistoryRepository transactionHistoryRepo;
    
    public List<TransactionHistory> findAllByUserId(Long userId) {
        return transactionHistoryRepo.findByUserId(userId);
    }
	

}
