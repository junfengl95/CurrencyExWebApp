package com.fdm.velocitytrade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fdm.velocitytrade.model.Bank;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.model.Wallet;
//import com.fdm.velocitytrade.model.WalletCurrency;
import com.fdm.velocitytrade.repo.BankRepository;
import com.fdm.velocitytrade.repo.TradeRepository;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;
import com.fdm.velocitytrade.repo.UserRepository;
//import com.fdm.velocitytrade.repo.WalletCurrencyRepository;
import com.fdm.velocitytrade.repo.WalletRepository;
import com.fdm.velocitytrade.service.UserService;

@Configuration
public class VelocityTradeConfig {

	private List<Trade> tradeList = new ArrayList<Trade>();
	LocalDateTime currentDateTime = LocalDateTime.now();
	Wallet wallet1;
	Wallet wallet2;
	Bank bank1;
	User user1;
	User user2;
	@Autowired
	UserService userSev;

	private Map<String, Double> currencyMap1 = new HashMap<String, Double>();
	private Map<String, Double> currencyMap2 = new HashMap<String, Double>();
	
	
	@Autowired
	UserService us;
	
	
	public VelocityTradeConfig() {

	}

	@Bean
	public User Test(UserRepository userRepo, TradeRepository tradeRepo, WalletRepository walletRepo,
			BankRepository bankRepo, TransactionHistoryRepository transactionHistoryRepo) {

		currencyMap1.put("SGD", 5000.0);
		currencyMap1.put("USD", 500.0);
		currencyMap1.put("HKD", 5000.0);
		user1 = new User("Bob", "password123", "bob@123.com", "USER", new Wallet(currencyMap1),
				new Bank("DBS", "abc123", "SGD"));
		currencyMap2.put("SGD", 20.0);
		currencyMap2.put("HKD", 10000.0);
		currencyMap2.put("USD", 500.0);
		user2 = new User("Jay", "1234bbb", "Jay@123.com", "USER", new Wallet(currencyMap2),
				new Bank("UOB", "abc123", "SGD"));

		userSev.registerUser(user1);
		userSev.registerUser(user2);
		// here the amount to and amount from is based on
		// amountTo = quantity * price (rate)
		// amountFrom = quantity

		wallet1 = walletRepo.save(user1.getWallet());
		wallet2 = walletRepo.save(user2.getWallet());

		bankRepo.save(user1.getBank());
		bankRepo.save(user2.getBank());

		user1 = userRepo.save(user1);
		user2 = userRepo.save(user2);

		// To simulate market trade both side
		// Market Buy Trade: sell 1000 SGD (also mean buying 750 USD)
		double amountFrom1 = 1000.0;
		String currencyFrom1 = "SGD";
		double price1 = 0.75;
		double amountTo1 = amountFrom1 * price1;
		String currencyTo1 = "USD";																																																		

		tradeList.add(
				new Trade(currencyFrom1, currencyTo1, ORDERTYPE.limit, price1, amountFrom1, amountTo1, "2025-02-08 17:00:00", 	 null, user1));

		// Market Sell Trade: sell 750 USD (also means buying 1000 SGD)
//		double amountFrom2 = 400.0;
//		String currencyFrom2 = "USD";
//		double price2 = 1 / price1;
//		double amountTo2 = amountFrom2 * price2; // => 400*1.33 = 534
//		String currencyTo2 = "SGD";
//		tradeList.add(new Trade(currencyFrom2, currencyTo2, ORDERTYPE.market, price2, amountFrom2, amountTo2,
//				"20/2/2024",  null, user2));
//		double amountFrom3 = 350.0;
//		double amountTo3 = amountFrom3 * price2; // => 350*1.33 = 466
//		tradeList.add(new Trade(currencyFrom2, currencyTo2, ORDERTYPE.market, price2, amountFrom3, amountTo3,
//				"20/2/2024",  null, user2));

		tradeList.add(new Trade("SGD", "HKD", ORDERTYPE.forward, 5.75, 1000.0, 1000.0 * 5.75, "2024-02-12 17:00:00",
				 "2024-02-23 17:00:00" , user1));
		tradeList.add(new Trade("KRW", "SGD", ORDERTYPE.limit, 900.0, 1000.0, 1000.0 * 900.0, "2024-06-12 17:00:00",
				 null, user1));
		
//		double price = 0.75;
//		double amountTo = 1000;
//		tradeList.add(new Trade("SGD", "HKD", ORDERTYPE.limit, price + 0.1, amountTo / price, amountTo, "2024-0-08 17:00:00",
//				 null, user1));
//		tradeList.add(new Trade("SGD", "HKD", ORDERTYPE.limit, price + 0.2, amountTo / price, amountTo, "20/2/2024",
//				 null, user1));
//		tradeList.add(new Trade("SGD", "HKD", ORDERTYPE.limit, price, amountTo / price, amountTo, "20/2/2024",
//				 null, user1));
//		tradeList.add(new Trade("SGD", "HKD", ORDERTYPE.limit, price, amountTo / price, amountTo, "20/2/2024",
//				 null, user1));
		

		tradeRepo.saveAll(tradeList);

		long matchedid=9;

		TransactionHistory th = new TransactionHistory(tradeList.get(0), TRADESTATUS.pending, matchedid);
		TransactionHistory th1 = new TransactionHistory(tradeList.get(0), TRADESTATUS.completed, 2);
//		TransactionHistory th2 = new TransactionHistory(tradeList.get(0), TRADESTATUS.completed, 3);
//		TransactionHistory th3 = new TransactionHistory(tradeList.get(1), TRADESTATUS.completed, 1);
//		TransactionHistory th4 = new TransactionHistory(tradeList.get(2), TRADESTATUS.completed, 1);
//		TransactionHistory th5 = new TransactionHistory(tradeList.get(3), TRADESTATUS.cancelled);
//		TransactionHistory th6 = new TransactionHistory(tradeList.get(4), TRADESTATUS.expired);
		transactionHistoryRepo.save(th1);
//		transactionHistoryRepo.save(th2);
//		transactionHistoryRepo.save(th3);
//		transactionHistoryRepo.save(th4);
//		transactionHistoryRepo.save(th5);
//		transactionHistoryRepo.save(th6);
//
//		System.out.println("INFO LOG User is : " + user1.toString());
//		System.out.println("INFO LOG wallet owner is : " + user1.getWallet().getUser().getUsername());
//		System.out.println("INFO LOG wallet id is : " + user1.getWallet().getWalletId());
//
//		System.out.println(tradeList.get(0).toString());
//		
//		
//		
//		System.out.println("ASSERT only 2 value "+tradeRepo.findMatchTradeIdenCurrencyAndTypeAndPrice(0.75, "HKD", "SGD"));
//		System.out.println("IS EMPTY? "+tradeRepo.findMatchTradeIdenCurrencyAndTypeAndPrice(0.1235, "HKD", "SGD").isEmpty());
		
//		System.out.println("ASSERT 4 values " + tradeRepo.findAndMatchAnyNumberTradesWithIdenticalCurrencyIdenticaltradeType("HKD", "SGD", ORDERTYPE.limit));
//		
//		
//
//		userSev.withdrawMoneyFromBank(user1.getUserId(), 100.0);
		userSev.depositMoneyToBank(user1.getUserId(), 10.0);
		Map<String, Double> currencyMap = userSev.getHoldingCurrencyByUserId(user1.getUserId());

		for (Map.Entry<String, Double> entry : currencyMap.entrySet()) {
			String currencyName = entry.getKey();
			Double amount = entry.getValue();
			System.out.println("Currency: " + currencyName + ", Amount: " + amount);
		}
		return user1;

		

	}
}