package com.fdm.velocitytrade.service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fdm.velocitytrade.TRADESTATUS;
import com.fdm.velocitytrade.exceptions.InsufficientFundsException;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.repo.TradeRepository;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;

@Service
public class DatabaseCheckForwardDate {

    @Autowired
    private TradeRepository tradeRepo;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepo;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 35 11 * * *") // This will run the method at 5:00 PM every day
    public void checkForwardDateEqualsEOD() {
    	// Combine with 17:00 (5:00 PM) to create LocalDateTime
    	LocalDate currentDate = LocalDate.now();
    	
    	// Combine with 17:00 (5:00 PM) to create LocalDateTime
        LocalDateTime endOfDay = currentDate.atTime(17, 0, 0);
       
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String endOfDayStringFormatted = endOfDay.format(formatter);

        
        // Query the database for records with forwardDate equal to EOD
        List<Optional<Trade>> tradeRecords = tradeRepo.findAllByForwardORDERTYPE(endOfDayStringFormatted);

        // Process the records as needed
        for (Optional<Trade> optionalTrade : tradeRecords) {
            if (optionalTrade.isPresent()) {
                Trade trade = optionalTrade.get();
                System.out.println("Transacting money from wallets..");
                System.out.println("This trade's user wallet before transact : " + userService.getHoldingCurrencyByUserId(trade.getUser().getUserId()));

                
                // Proceed with further processing
//                userService.withdrawMoneyFromWallet(trade.getUser().getUserId(), trade.getCurrencyFrom(), trade.getAmountFrom());
//                userService.depositMoneyToWallet(trade.getUser().getUserId(), trade.getCurrencyTo(), trade.getAmountTo());
//                System.out.println("This trade's user wallet after transact : " + userService.getHoldingCurrencyByUserId(trade.getUser().getUserId()));
//                
//                System.out.println("Trade ID: " + trade.getTradeId() + " will be moved from Trade to TransactionHistory DB");	
//                transactionHistoryRepo.save(new TransactionHistory(trade, TRADESTATUS.completed));
//                tradeRepo.delete(trade);
//                System.out.println("INFO LOG: FORWARD TRADE:" + trade.getTradeId() + " SETTLED");
                
                try {
                    // Attempt to withdraw money from the wallet
                    boolean moneyWithdrawn = userService.withdrawMoneyFromWallet(trade.getUser().getUserId(), trade.getCurrencyFrom(), trade.getAmountFrom());

                    if (moneyWithdrawn) {
                        userService.depositMoneyToWallet(trade.getUser().getUserId(), trade.getCurrencyTo(), trade.getAmountTo());
                        System.out.println("This trade's user wallet after transact : " + userService.getHoldingCurrencyByUserId(trade.getUser().getUserId()));
                        
                        System.out.println("Trade ID: " + trade.getTradeId() + " will be moved from Trade to TransactionHistory DB");   
                        transactionHistoryRepo.save(new TransactionHistory(trade, TRADESTATUS.completed));
                        tradeRepo.delete(trade);
                        System.out.println("INFO LOG: FORWARD TRADE:" + trade.getTradeId() + " SETTLED");
                    }
                } catch (InsufficientFundsException e) {
                    // If insufficient funds, cancel the trade
                	trade.setTradeStatus(TRADESTATUS.cancelledInsufficientfund);
                	tradeRepo.delete(trade);
                    System.out.println("INFO LOG: Trade ID " + trade.getTradeId() + " canceled due to insufficient funds.");
                
                } 
            }
            else {
                System.out.println("INFO LOG: NO FORWARD TRADE TO SETTLE EOD");
            }
        }
    }
}