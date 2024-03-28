package com.fdm.velocitytrade.transaction;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fdm.velocitytrade.Global;
import com.fdm.velocitytrade.ORDERTYPE;
import com.fdm.velocitytrade.TRADESTATUS;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.Wallet;
import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;
import com.fdm.velocitytrade.service.TradeService;
import com.fdm.velocitytrade.service.UserService;

@Component
public class Transactions {

	@Autowired
	private TradeService tradeService;

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionHistoryRepository transactionHistoryRepo;
	
	@Autowired
	Global global;
	
	public Transactions() {

		
	}
	
	public final void deductFunds(Trade incomingTrade) {

		userService.withdrawMoneyFromWallet(incomingTrade.getUser().getUserId(), incomingTrade.getCurrencyFrom(), incomingTrade.getAmountFrom());

	}
	
	
	// if the matched trade has a greater amount, then it should remain in the
	// exchange but its amount from reduced DB while the user's wallet is updated
	// with the new currency

	// If the matched trade has a smaller amount, it should be removed from DB
	// the incoming trade will have its amountTo and currency reduced, then saved to
	// DB
	public final Optional<Trade> transact(Trade matchedTrade, Trade incomingTrade) {

		boolean matchedHigher;
		double diff = matchedTrade.getAmountFrom() - incomingTrade.getAmountTo();
		diff = global.isZero(global.round(diff));
		
		if (diff > 0) {

			matchedHigher = true;
			System.out.println("DEBUG LOG matched trade is higher amount. Amount difference is : " + diff);
			return Optional.of(transactUnequalAmounts(matchedTrade, incomingTrade, matchedHigher));

		} else if (diff < 0) {

			matchedHigher = false;
			System.out.println("DEBUG LOG matched trade is lower amount. Amount difference is : " + diff);
			return Optional.of(transactUnequalAmounts(matchedTrade, incomingTrade, matchedHigher));

		} else {

			System.out.println("DEBUG LOG The amount diff is zero.");
			transactExactAmount(matchedTrade, incomingTrade);
			return Optional.empty();

		}

	}

	// For when exact amount matched
	private final void transactExactAmount(Trade matchedTrade, Trade incomingTrade) {

		// If ORDERTYPE is not a forward, continue with original logic.
		if (matchedTrade.getOrderType() != ORDERTYPE.forward) {
			// Remove matchedtrade from database
			System.out.println(
					"DEBUG LOG Removed from database " + matchedTrade + " result : " + tradeService.remove(matchedTrade));
	
			// get users
			User matchedUser = matchedTrade.getUser();
			User incomingUser = incomingTrade.getUser();
	
			System.out.println("Transacting money from wallets..");
			// Update wallet values
	
	
			System.out.println("Matched user wallet before transact : " + userService.getHoldingCurrencyByUserId(matchedUser.getUserId()));
			System.out.println("Incoming user wallet beforetransact : " + userService.getHoldingCurrencyByUserId(incomingUser.getUserId()));
			
	//		userService.withdrawMoneyFromWallet(matchedUser.getUserId(), matchedTrade.getCurrencyFrom(),
	//				matchedTrade.getAmountFrom());
			userService.depositMoneyToWallet(matchedUser.getUserId(), matchedTrade.getCurrencyTo(),
					matchedTrade.getAmountTo());
			
	//		userService.withdrawMoneyFromWallet(incomingUser.getUserId(), incomingTrade.getCurrencyFrom(),
	//				incomingTrade.getAmountFrom());
			userService.depositMoneyToWallet(incomingUser.getUserId(), incomingTrade.getCurrencyTo(),
					incomingTrade.getAmountTo());
	
			TransactionHistory transctionHistory1 = new TransactionHistory(matchedTrade, TRADESTATUS.completed);
			TransactionHistory transctionHistory2 = new TransactionHistory(incomingTrade, TRADESTATUS.completed);
			transactionHistoryRepo.save(transctionHistory1);
			transactionHistoryRepo.save(transctionHistory2);
			
			System.out.println("Matched user wallet after transact : " + userService.getHoldingCurrencyByUserId(matchedUser.getUserId()));
			System.out.println("Incoming user wallet after transact : " + userService.getHoldingCurrencyByUserId(incomingUser.getUserId()));
		}
		
		// If ORDER TYPE IS A FORWARD, do not update wallet. update matchedTrade trade status in DB to ForwardMatchedPendingSettlement and save incoming trade in Trade DB
		else {
			matchedTrade.setTradeStatus(TRADESTATUS.forwardMatchedPendingSettlement);
			tradeService.update(matchedTrade.getTradeId(), matchedTrade);
			incomingTrade.setTradeStatus(TRADESTATUS.forwardMatchedPendingSettlement);
			tradeService.save(incomingTrade);
			
			System.out.println("INFO LOG: New Forward Trade " + incomingTrade.getTradeId() + " is matched with Existing Forward Trade " + matchedTrade.getTradeId());
			System.out.println("INFO LOG: Existing Forward Trade " + matchedTrade.getTradeId() + " tradeStatus is updated in Trade DB");
			System.out.println("INFO LOG: New Forward Trade " + incomingTrade.getTradeId() + " tradeStatus is saved in Trade DB");
			System.out.println("Matched Trades will settle on Forward Date: " + incomingTrade.getForwardDate());
			
		}
	}

	
	
	
	
	// for when not exact amount matched
	private Trade transactUnequalAmounts(Trade matchedTrade, Trade incomingTrade, boolean matchedHigher) {
		Trade updatedTrade = null;
		
		//When the matched trade(one that is in DB) has a higher amount, thus will remain stored in DB
		if (matchedHigher) {

			updatedTrade = new Trade();

			//Copy the trade to update
			updatedTrade.deepCopy(matchedTrade);
			
			// if the OrderType != forward
			if (updatedTrade.getOrderType() != ORDERTYPE.forward) {
				double amountAfterTransaction = updatedTrade.getAmountFrom() - incomingTrade.getAmountTo();
				updatedTrade.setAmountFrom(amountAfterTransaction);
				updatedTrade.setAmountTo(amountAfterTransaction * updatedTrade.getPrice());
	
				// update db trade amount
				tradeService.update(matchedTrade.getTradeId(), updatedTrade);
				
				//save the completed transaction
				TransactionHistory transctionHistory1 = new TransactionHistory(incomingTrade, TRADESTATUS.completed);
				transactionHistoryRepo.save(transctionHistory1);
	
				
				// get users
				User matchedUser = matchedTrade.getUser();
				User incomingUser = incomingTrade.getUser();
	
				System.out.println("DEBUG LOG Transacting money from wallets..");
	
				System.out.println("Matched user wallet before transact : " + userService.getHoldingCurrencyByUserId(matchedUser.getUserId()));
				System.out.println("Incoming user wallet beforetransact : " + userService.getHoldingCurrencyByUserId(incomingUser.getUserId()));
				
				// minus off the currecncy that was exchanged, i.e., the incoming trade's TO
				// currency
	//			userService.withdrawMoneyFromWallet(matchedUser.getUserId(), incomingTrade.getCurrencyTo(),
	//					incomingTrade.getAmountTo());
	
				// Add the currency that was exchanged, i.e., the incoming trade's FROM currency
				userService.depositMoneyToWallet(matchedUser.getUserId(), incomingTrade.getCurrencyFrom(),
						incomingTrade.getAmountFrom());
	
				// Minus currency that was exchanged, i.e., incoming Trade FROM currency
	//			userService.withdrawMoneyFromWallet(incomingUser.getUserId(), incomingTrade.getCurrencyFrom(),
	//					incomingTrade.getAmountFrom());
	
				// Add the currency that was exchanged, i.e., the incoming trade's TO currency
				userService.depositMoneyToWallet(incomingUser.getUserId(), incomingTrade.getCurrencyTo(),
						incomingTrade.getAmountTo());
	
				System.out.println("Matched user wallet after transact : " + userService.getHoldingCurrencyByUserId(matchedUser.getUserId()));
				System.out.println("Incoming user wallet after transact : " + userService.getHoldingCurrencyByUserId(incomingUser.getUserId()));
				
				return updatedTrade;
			}
			
			// if the OrderType = forward
			// Note: Matched trade amount in DB > incoming trade amount
			// update the matchedTrade in DB with the updated amounts and saved the incomingTrade into trade DB with tradeStatus = forwardMatchedPendingSettlement
			// No need to update transactionHistoryRepo cause forward trades are considered completed only on forward date
			// No need to update wallet upon matched cause forward trades will only update wallet upon forward date.
			else {
				double amountAfterTransaction = updatedTrade.getAmountFrom() - incomingTrade.getAmountTo();
				updatedTrade.setAmountFrom(amountAfterTransaction);
				updatedTrade.setAmountTo(amountAfterTransaction * updatedTrade.getPrice());
				// update db trade amount
				tradeService.update(matchedTrade.getTradeId(), updatedTrade);
				incomingTrade.setTradeStatus(TRADESTATUS.forwardMatchedPendingSettlement);
				tradeService.save(incomingTrade);
	
				System.out.println("INFO LOG: Forward Trade " + incomingTrade.getTradeId() + " is matched with Forward Trade " + updatedTrade.getTradeId());
				System.out.println("INFO LOG: Forward Trade " + updatedTrade.getTradeId() + " is updated in DB");
				System.out.println("Matched Forward Trade will settle on Forward Date: " + incomingTrade.getForwardDate());
				
				return updatedTrade;
			}

		} 
		
		//When the incoming trade(one that is NOT YET in DB) has a higher amount, thus TO BE stored in DB
		else {
			
			updatedTrade = new Trade();
		
			//Copy the trade to update
			updatedTrade.deepCopy(incomingTrade);
			
			// if the OrderType != forward
			if (updatedTrade.getOrderType() != ORDERTYPE.forward) {
				double amountAfterTransaction = updatedTrade.getAmountFrom() - matchedTrade.getAmountTo();
				updatedTrade.setAmountFrom(amountAfterTransaction);
				updatedTrade.setAmountTo(amountAfterTransaction * updatedTrade.getPrice());
	
				// remove the fulfilled matched trade and save the updated trade
				tradeService.remove(matchedTrade);
				tradeService.save(updatedTrade);
	
				// save the completed transaction
				TransactionHistory transctionHistory1 = new TransactionHistory(matchedTrade, TRADESTATUS.completed);
				transactionHistoryRepo.save(transctionHistory1);
				
				
				// get users
				User matchedUser = matchedTrade.getUser();
				User incomingUser = updatedTrade.getUser();
	
				System.out.println("DEBUG LOG Transacting money from wallets..");
	
				System.out.println("Matched user wallet before transact : " + userService.getHoldingCurrencyByUserId(matchedUser.getUserId()));
				System.out.println("Incoming user wallet beforetransact : " + userService.getHoldingCurrencyByUserId(incomingUser.getUserId()));
				
				// Add the currency that was exchanged, i.e., the matchedTrade  FROM currency
				userService.depositMoneyToWallet(matchedUser.getUserId(), matchedTrade.getCurrencyTo(),
						matchedTrade.getAmountTo());
				
				// minus off the currecncy that was exchanged, i.e., the matchedTrade TO currency
	//			userService.withdrawMoneyFromWallet(matchedUser.getUserId(), matchedTrade.getCurrencyFrom(),
	//					matchedTrade.getAmountFrom());
	
				// Minus currency that was exchanged, i.e., matchedTrade  FROM currency
	//			userService.withdrawMoneyFromWallet(incomingUser.getUserId(), matchedTrade.getCurrencyTo(),
	//					matchedTrade.getAmountTo());
	
				// Add the currency that was exchanged, i.e., the matchedTrade  TO currency
				userService.depositMoneyToWallet(incomingUser.getUserId(), matchedTrade.getCurrencyFrom(),
						matchedTrade.getAmountFrom());
	
				
				System.out.println("Matched user wallet after transact : " + userService.getHoldingCurrencyByUserId(matchedUser.getUserId()));
				System.out.println("Incoming user wallet after transact : " + userService.getHoldingCurrencyByUserId(incomingUser.getUserId()));
				
				return updatedTrade;
			}
			
			// if the OrderType = forward
			// Note: incoming trade amount > matched trade amount in DB
			// update the matchedTrade in DB with tradeStatus = forwardMatchedPendingSettlement and deduct incoming trade amount with matchedTrade amount and then save incoming trade
			// No need to update transactionHistoryRepo cause forward trades are considered completed only on forward date
			// No need to update wallet upon matched cause forward trades will only update wallet upon forward date.
			else {

					double amountAfterTransaction = updatedTrade.getAmountFrom() - matchedTrade.getAmountTo();
					updatedTrade.setAmountFrom(amountAfterTransaction);
					updatedTrade.setAmountTo(amountAfterTransaction * updatedTrade.getPrice());					
					tradeService.save(updatedTrade);
					
					// For matchedTrade, since it is entirely matched, update trade status to forwardMatchedPendingSettlement and update Trade DB
					matchedTrade.setTradeStatus(TRADESTATUS.forwardMatchedPendingSettlement);
					tradeService.update(matchedTrade.getTradeId(), matchedTrade);
					
					System.out.println("INFO LOG: Forward Trade " + incomingTrade.getTradeId() + " is matched with Forward Trade " + updatedTrade.getTradeId());
					System.out.println("INFO LOG: Forward Trade " + updatedTrade.getTradeId() + " is updated in DB");
					System.out.println("Matched Forward Trade will settle on Forward Date: " + incomingTrade.getForwardDate());
					
					return updatedTrade;
				
			}
		}

	}

	//this method will refund users money when deleting a trade
	public void refund(Trade obj) {

		
		// TODO Auto-generated method stub
		

	}
	
	public void refundForCheckExpiryDateFunction(Trade obj) {
		userService.withdrawMoneyFromWallet(obj.getUser().getUserId(), obj.getCurrencyTo(), obj.getAmountTo());
		userService.depositMoneyToWallet(obj.getUser().getUserId(), obj.getCurrencyFrom(), obj.getAmountFrom());
		
		
		// TODO Auto-generated method stub
		

	}

}
