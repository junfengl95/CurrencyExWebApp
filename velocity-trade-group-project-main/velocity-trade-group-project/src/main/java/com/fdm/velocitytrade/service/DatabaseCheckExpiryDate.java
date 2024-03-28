package com.fdm.velocitytrade.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fdm.velocitytrade.ORDERTYPE;
import com.fdm.velocitytrade.TRADESTATUS;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.repo.TradeRepository;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;
import com.fdm.velocitytrade.transaction.Transactions;

@Service
public class DatabaseCheckExpiryDate {

    @Autowired
    private TradeRepository tradeRepo;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepo;

    @Autowired
    private UserService userService;
    
    @Autowired
    private Transactions transactions;

    @Scheduled(cron = "0 35 11 * * *") // This will run the method at 5:00 PM every day
    public void checkExpiryDateEqualsEOD() {
    	// Combine with 17:00 (5:00 PM) to create LocalDateTime
    	LocalDate currentDate = LocalDate.now();
    	
    	// Combine with 17:00 (5:00 PM) to create LocalDateTime
        LocalDateTime endOfDay = currentDate.atTime(17, 0, 0);
       
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String endOfDayStringFormatted = endOfDay.format(formatter);
        
        // Query the database for records with forwardDate equal to EOD
        List<Optional<Trade>> tradeRecords = tradeRepo.findAllExpiryDateEqualEOD(endOfDayStringFormatted);
        
        // Process the records as needed
        for (Optional<Trade> optionalTrade : tradeRecords) {
            if (optionalTrade.isPresent()) {
                Trade trade = optionalTrade.get();
                
                // Check if ordertype is forward. If forward, directly move from Trade to TransactionHistory DB.
                // No need to update wallet since Forward trades do not transact wallet when pending.
                if (trade.getOrderType() == ORDERTYPE.forward) {
                    transactionHistoryRepo.save(new TransactionHistory(trade, TRADESTATUS.expired));
                    tradeRepo.delete(trade);
                    System.out.println("Expired trade is a forward trade and it is removed from Trade DB");
                }
                
                // If this is a market or limit trade, we need to refund the money back given trade is expired.
                else {
                    transactionHistoryRepo.save(new TransactionHistory(trade, TRADESTATUS.expired));
                	transactions.refundForCheckExpiryDateFunction(trade);
            		tradeRepo.delete(trade);
                    System.out.println("Expired trade is a market/limit trade. Amounts are "
                    		+ "refunded to user's wallet and it is removed from Trade DB");
                }
        
    }
}
    }
}
