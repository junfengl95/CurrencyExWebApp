package com.fdm.velocitytrade.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fdm.velocitytrade.ORDERTYPE;
import com.fdm.velocitytrade.TRADESTATUS;
import com.fdm.velocitytrade.exceptions.InsufficientFundsException;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;
import com.fdm.velocitytrade.service.TradeService;
import com.fdm.velocitytrade.transaction.Transactions;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

	@Autowired
	private TradeService tradeService;

	@Autowired
	private Transactions transactions;

	@Autowired
	private TransactionHistoryRepository transactionHistoryRepo;

	@GetMapping("/all")
	public List<Trade> retrieveAllTrades() {

		List<Trade> tradeList = tradeService.findAll();
		return tradeList;

	}
	@GetMapping("/allStatusPending")
	public List<Trade> findAllByTradeStatusPending() {

		List<Trade> tradeList = tradeService.findAllByTradeStatusPending();
		return tradeList;

	}
	
	@GetMapping("/allForward/{userId}")
	public List<Optional<Trade>> retrieveAllForwardTradesOnly(@PathVariable Long userId) {

		List<Optional<Trade>> tradeList = tradeService.findByUserIdAndOnlyForwardORDERTYPEAndStatusForwardMatchedPendingSettlement(userId);
		return tradeList;

	}

	@GetMapping("/{id}")
	public Trade retrieveSingleTrade(@PathVariable("id") long id) {

		return tradeService.find(id);

	}
	


	@PostMapping("/{id}")
	public Trade retrieveSingleTrade(@PathVariable("id") long id, @RequestBody Trade incomingTrade) {

		return tradeService.find(incomingTrade);

	}

	@PostMapping(value = "/save")
	public Optional<ResponseEntity<Trade>> saveNewTrade(@RequestBody Trade incomingTrade) throws InsufficientFundsException {

		Trade savedTrade = null;
		URI location = null;
		Optional<Trade> matchedTrade = tradeService.findMatches(incomingTrade);
		
		// For Market and Limit trades, we deduct amount from wallet as long as trade executed. For Forward, no amount is deducted.
		if (incomingTrade.getOrderType() != ORDERTYPE.forward) {
			transactions.deductFunds(incomingTrade);
		}
		
		if (matchedTrade.isEmpty()) {

			System.out.println("DEBUG LOG NO MATCHING TRADE FOUND");
			System.out.println("DEBUG LOG SAVING INTO DB");
			savedTrade = tradeService.save(incomingTrade);

			
			location = ServletUriComponentsBuilder.fromCurrentRequest().path("").buildAndExpand(savedTrade.getTradeId())
					.toUri();
			return Optional.of(ResponseEntity.created(location).body(savedTrade));

		} else {

			System.out.println("DEBUG LOG MATCHING TRADE FOUND");
			System.out.println("DEBUG LOG TRADE ID OF MATCHED TRADE IS : " + matchedTrade.get().getTradeId()
					+ " TRADE DETAILS : " + matchedTrade);
			Optional<Trade> remainingTrade = transactions.transact(matchedTrade.get(), incomingTrade);

			// if present we return the updated trade values
			if (remainingTrade.isPresent()) {

				location = ServletUriComponentsBuilder.fromCurrentRequest().path("")
						.buildAndExpand(remainingTrade.get().getTradeId()).toUri();

				// return the trade with remaining amount
				return Optional.of(ResponseEntity.created(location).body(remainingTrade.get()));

			} else {

				// returns null
				return Optional.of(ResponseEntity.created(location).body(new Trade()));

			}

		}

	}

	@PostMapping(value = "/update/{id}")
	public Optional<ResponseEntity<Trade>> updateTrade(@PathVariable("id") long tradeId, @RequestBody Trade incomingTrade) throws InsufficientFundsException {

		Trade updatedTrade = tradeService.update(tradeId, incomingTrade);

		Trade savedTrade = null;
		URI location = null;
		Optional<Trade> matchedTrade = tradeService.findMatches(updatedTrade);

		if (matchedTrade.isEmpty()) {

			System.out.println("DEBUG LOG NO MATCHING TRADE FOUND");
			System.out.println("DEBUG LOG SAVING INTO DB");

			return Optional.of(ResponseEntity.created(location).body(updatedTrade));

		} else {

			System.out.println("DEBUG LOG MATCHING TRADE FOUND");
			System.out.println("DEBUG LOG TRADE ID OF MATCHED TRADE IS : " + matchedTrade.get().getTradeId()
					+ " TRADE DETAILS : " + matchedTrade);

			Optional<Trade> remainingTrade = transactions.transact(matchedTrade.get(), incomingTrade);

			// if present we return the updated trade values
			if (remainingTrade.isPresent()) {

				location = ServletUriComponentsBuilder.fromCurrentRequest().path("")
						.buildAndExpand(remainingTrade.get().getTradeId()).toUri();

				// return the trade with remaining amount
				return Optional.of(ResponseEntity.created(location).body(remainingTrade.get()));

			} else {

				// returns null
				return Optional.of(ResponseEntity.created(location).body(new Trade()));

			}

		}
	}
	
	// check if anyone using this API cause gonna implement the refund
	@PostMapping(value = "/delete/{id}")
	public boolean removeTrade(@PathVariable("id") long tradeId) {

		Trade cancellTrade = retrieveSingleTrade(tradeId);

		TransactionHistory newRecord = new TransactionHistory(cancellTrade, TRADESTATUS.cancelled);

		transactionHistoryRepo.save(newRecord);

		return tradeService.remove(tradeService.find(tradeId));

	}

}
