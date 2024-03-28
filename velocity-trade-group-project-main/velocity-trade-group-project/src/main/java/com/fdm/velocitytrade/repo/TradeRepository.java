package com.fdm.velocitytrade.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fdm.velocitytrade.ORDERTYPE;
import com.fdm.velocitytrade.model.Trade;


@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {


	/*
	 * find And Match Trades With Identical Currency Identical tradeType Identical price
	 * This query will match any trade that has the following
	 * CurrenyTO is the same as incoming trade's curreny FROM
	 * trade will match limit trades
	 */
	@Query("SELECT t "
			+ "FROM Trade t "
			+ "WHERE t.price >= :price "
			+ "AND t.currencyTo like :currencyfrom "
			+ "AND t.currencyFrom like :currencyto "
			+ "AND (t.orderType = limit OR t.orderType = market) "
			+ "order by t.price asc, t.executionDate asc")
	List<Optional<Trade>> findMatchTradeIdenCurrencyAndTypeAndPrice(
			@Param("price") double price,
			@Param("currencyfrom") String currencyfrom,
			@Param("currencyto") String currencyto
//			@Param("tradetype") ORDERTYPE tradeType
			);
	
	/*
	 * find forward trades to match
	 */
	@Query("SELECT t "
			+ "FROM Trade t "
			+ "WHERE t.price >= :price "
			+ "AND t.currencyTo like :currencyfrom "
			+ "AND t.currencyFrom like :currencyto "
			+ "AND t.orderType = :tradetype "
			+ "AND t.forwardDate = :forwardDate "
			+ "order by t.price asc, t.executionDate asc")
	List<Optional<Trade>> findMatchForwardTrade(
			@Param("price") double price,
			@Param("currencyfrom") String currencyfrom,
			@Param("currencyto") String currencyto,
			@Param("tradetype") ORDERTYPE tradeType,
			@Param("forwardDate") String forwardDate
			);
	

	@Query("SELECT t FROM Trade t WHERE t.orderType = com.fdm.velocitytrade.ORDERTYPE.forward AND t.forwardDate = :todayEOD")
	List<Optional<Trade>> findAllByForwardORDERTYPE(@Param("todayEOD") String endOfDay);
	
	@Query("SELECT t FROM Trade t WHERE t.tradeStatus =com.fdm.velocitytrade.TRADESTATUS.pending")
	List<Trade> findAllByTradeStatusPending();
	
	
	@Query("SELECT t FROM Trade t JOIN FETCH t.user u WHERE u.id = :userId "
			+ "AND t.orderType = com.fdm.velocitytrade.ORDERTYPE.forward "
			+ "AND t.tradeStatus =com.fdm.velocitytrade.TRADESTATUS.forwardMatchedPendingSettlement ")
    List<Optional<Trade>>findByUserIdAndOnlyForwardORDERTYPEAndStatusForwardMatchedPendingSettlement(@Param("userId") Long userId);

	@Query("SELECT t FROM Trade t WHERE t.expiryDate = :todayEOD")
	List<Optional<Trade>> findAllExpiryDateEqualEOD(@Param("todayEOD") String endOfDay);
	
//    @Query("SELECT th FROM TransactionHistory th WHERE th.userId = :userId AND u.orderType = 'forward'")
//    List<Optional<Trade>>findByUserIdAndOnlyForwardORDERTYPE(@Param("userId") Long userId);
	
	/*
	 * This query will match any trade that has the following
	 * CurrenyTO is the same as incoming trade's curreny FROM
	 * trade will match limit trades
	 */
//	@Query("SELECT t "
//			+ "FROM Trade t "
//			+ "WHERE t.currencyTo like :currencyfrom "
//			+ "AND t.currencyFrom like :currencyto "
//			+ "AND t.orderType = :tradetype "
//			+ "order by t.executionDate asc")
//	List<Optional<Trade>> findAndMatchAnyNumberTradesWithIdenticalCurrencyIdenticaltradeType(
//			@Param("currencyfrom") String currencyfrom,
//			@Param("currencyto") String currencyto,
//			@Param("tradetype") ORDERTYPE tradeType
//			);
//	
	
	
	
}
