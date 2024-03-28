package com.fdm.velocitytrade.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fdm.velocitytrade.Global;
import com.fdm.velocitytrade.ORDERTYPE;
import com.fdm.velocitytrade.exceptions.TradeExistsException;
import com.fdm.velocitytrade.exceptions.TradeNotFoundException;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.repo.TradeRepository;
import com.fdm.velocitytrade.transaction.Transactions;

// General overview
// if the user cant be found -> TradeNotFoundException is thrown
// if trying to save over an existing user and not using update -> TradeExistsException IsThrown

@Service
public class TradeService {

	@Autowired
	TradeRepository tradeRepository;

	@Autowired
	Global global;

//	@Autowired
//	Transactions transactions;

	public TradeService() {

	}

	public List<Trade> findAll() {

		return tradeRepository.findAll();

	}
	
	public List<Trade> findAllByTradeStatusPending() {

		return tradeRepository.findAllByTradeStatusPending();

	}
	
	public List<Optional<Trade>> findByUserIdAndOnlyForwardORDERTYPEAndStatusForwardMatchedPendingSettlement(Long userId){
		return tradeRepository.findByUserIdAndOnlyForwardORDERTYPEAndStatusForwardMatchedPendingSettlement(userId);
	}

	public List<Trade> find(List<Trade> listObj) {

		List<Long> listId = listObj.stream().map(Trade::getTradeId) // User::getId references the class method
																	// signature
				.collect(Collectors.toList()); // convert to list

		return tradeRepository.findAllById(listId);

	}

	public Trade find(Trade obj) {

		long id = obj.getTradeId();

		Optional<Trade> tradeOptional = tradeRepository.findById(id);

		return tradeOptional.orElseThrow(() -> new TradeNotFoundException("Trade with ID " + id + " not found."));

	}

	public Trade find(long id) {

		Optional<Trade> tradeOptional = tradeRepository.findById(id);

		return tradeOptional.orElseThrow(() -> new TradeNotFoundException("Trade with ID " + id + " not found."));

	}

	// only used for saving new entities, to update existing, use update()
	public Trade save(Trade obj) {

		// This check is necessary as creating a object should not provide a
		// tradeidNumber
		// thus, although id is primitive long, it is not initialised by us (hence
		// leading to null when creating POJO from deserialisation)

		long id = obj.getTradeId();

		// we can directly return if the ID is zero.
		// This is because a zero value signifies the object is uninitalised in DB

		Optional<Trade> tradeOptional = tradeRepository.findById(id);

		tradeOptional.ifPresentOrElse(value -> {

			// throw if error
			throw new TradeExistsException(
					"Entity already exists: " + value + ". If action is to update, use /update/id");

		},

				() -> {

					// round before setting into the DB
					obj.setPrice(global.round(obj.getPrice()));

					// Save the entity
					tradeRepository.save(obj);

				});

		return obj;

	}

	public List<Trade> save(List<Trade> listObj) {

		List<Trade> applications = tradeRepository.saveAll(listObj);

		return applications;

	}

	// returns true if successfully removed
//	public boolean remove(long id) {
//
//		Trade obj = this.find(id);
//		transactions.refund(obj);
//		tradeRepository.deleteById(id);
//		return tradeRepository.findById(id).isEmpty();
//
//	}

	public boolean remove(Trade obj) {

//		transactions.refund(obj);
		tradeRepository.delete(obj);
		return tradeRepository.findById(obj.getTradeId()).isEmpty();

	}

	public void remove(List<Trade> listObj) {

		tradeRepository.flush();
		tradeRepository.deleteAllInBatch(listObj);

	}

	public Trade update(long targetObjid, Trade sourceObj) {

		// We directly call the save method here because we want to override the
		// existing entry
		return tradeRepository.save(this.find(targetObjid).update(sourceObj));

	}

	public Optional<Trade> findMatches(Trade incomingTrade) {

		final int earliestExecutionDate = 0;

		// inverse the rate because matching someone that is selling
		double inverseRate = 1 / incomingTrade.getPrice();
		inverseRate = global.round(inverseRate);

		// Additional logic to check if the trade is forward trade
		if (incomingTrade.getOrderType() == ORDERTYPE.forward) {

			List<Optional<Trade>> matches = tradeRepository.findMatchForwardTrade(inverseRate,
					incomingTrade.getCurrencyFrom(), incomingTrade.getCurrencyTo(), incomingTrade.getOrderType(),
					incomingTrade.getForwardDate());

			if (matches.isEmpty()) {

				return Optional.empty();

			} else {

				return matches.get(earliestExecutionDate);

			}

		}

		// Original logic no change.
		else {

			List<Optional<Trade>> matches = tradeRepository.findMatchTradeIdenCurrencyAndTypeAndPrice(inverseRate,
					incomingTrade.getCurrencyFrom(), incomingTrade.getCurrencyTo());
			System.out.println("The matches from repo" + matches);
			if (matches.isEmpty()) {

				return Optional.empty();

			} else {

				return matches.get(earliestExecutionDate);

			}

		}

	}

}
