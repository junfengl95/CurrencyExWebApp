package com.fdm.velocitytrade.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;
import com.fdm.velocitytrade.service.TradeService;
import com.fdm.velocitytrade.service.UserService;
import com.fdm.velocitytrade.transaction.Transactions;

//@RunWith(SpringRunner.class)
//@SpringBootTest(
//  webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@SpringBootTest(
//		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//		classes = Application.class)
//@AutoConfigureMockMvc
//@TestPropertySource(
//  locations = "classpath:application-integrationtest.properties")

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TransactionTests {

	@Autowired
	Transactions transactions;

	@MockBean
	TradeService tradeService;
	
	@MockBean
	UserService userService;
	
	@MockBean
	TransactionHistoryRepository transactionHistoryRepository;
	
	@Mock
	Trade tradeInDb;

	@Mock
	Trade incomingTrade;

	@Mock
	User user;
	@Mock
	User user2;
	
	@BeforeEach
	void initialise() {

	}

	@Test
	@DisplayName("VM connect to datasource")
	void contextLoads() {

		// if fails, most likely your VM args do not contain username and password.
		// JUnit has separate VM args compared to Java app.
		assertEquals(1, 1);

	}

	@Test
	@DisplayName("Make a transaction whose trade amounts are exact")
	void testMakeTradeExact() {

		// Arrange
		// act
		boolean empty = true;
		long testUserId = 500;
		double testAmountTo = 200.00;
		double testAmountFrom = testAmountTo;
		when(user.getUserId()).thenReturn(testUserId);
		when(incomingTrade.getAmountTo()).thenReturn(testAmountTo);
		when(incomingTrade.getUser()).thenReturn(user);
		when(tradeInDb.getAmountFrom()).thenReturn(testAmountFrom);
		when(tradeInDb.getUser()).thenReturn(user);
		
		Optional<Trade> retTrade = transactions.transact(tradeInDb, incomingTrade);
		
		// Assert
		assertEquals(empty, retTrade.isEmpty());
		
		
//		assertThrows(TradeExistsException.class, () -> {
//
//			// Code that should throw TradeExistsException
//			Trade retTrade = tradeService.save(incomingTrade);
//
//		}, "no such trade");

	}

	//This tests that when the amount difference are exactly the same for the first 6d.p.
	@Test
	@DisplayName("Make a transaction whose trade amounts are exact till 6d.p.")
	void testMakeTradeExactHighDecimalPlaces() {
		
		// Arrange
		// act
		boolean empty = true;
		long testUserId = 500;
		double testAmountTo = 200.000000_123457;
		double testAmountFrom = 200.00;
		when(user.getUserId()).thenReturn(testUserId);
		when(incomingTrade.getAmountTo()).thenReturn(testAmountTo);
		when(incomingTrade.getUser()).thenReturn(user);
		when(tradeInDb.getAmountFrom()).thenReturn(testAmountFrom);
		when(tradeInDb.getUser()).thenReturn(user);
		
		Optional<Trade> retTrade = transactions.transact(tradeInDb, incomingTrade);
		
		// Assert
		assertEquals(empty, retTrade.isEmpty());
		
	}
	
	//This tests that when the amount difference are exactly the same for the first 6d.p.
	@Test
	@DisplayName("Make a transaction whose trade amounts are not exact due to decimal placing")
	void testMakeTradePartialHighDecimalPlaces() {
		
		// Arrange
		// act
		boolean present = true;
		long testUserId = 500;
		long testIncomingTradeId = 1000;
		double testAmountTo = 200.000001_0123;
		double testAmountFrom = 200.00;
		when(user.getUserId()).thenReturn(testUserId);
		when(incomingTrade.getTradeId()).thenReturn(testIncomingTradeId);
		when(incomingTrade.getAmountTo()).thenReturn(testAmountTo);
		when(incomingTrade.getUser()).thenReturn(user);
		when(tradeInDb.getAmountFrom()).thenReturn(testAmountFrom);
		when(tradeInDb.getUser()).thenReturn(user);
		
		Optional<Trade> retTrade = transactions.transact(tradeInDb, incomingTrade);
		
		// Assert
		assertEquals(present, retTrade.isPresent());
		assertEquals(testIncomingTradeId, retTrade.get().getTradeId());
		
	}
	
	//This tests that when the amount difference are exactly the same for the first 6d.p.
	@Test
	@DisplayName("Make a transaction whose trade amounts are not exact due to non-exact amount when incoming trade higher")
	void testMakeTradePartialAmount() {
		
		// Arrange
		// act
		boolean present = true;
		long testUserId = 500;
		long testIncomingTradeId = 1000;
		double testAmountTo = 1000.000000;
		double testAmountFrom = 200.00;
		when(user.getUserId()).thenReturn(testUserId);
		when(incomingTrade.getTradeId()).thenReturn(testIncomingTradeId);
		when(incomingTrade.getAmountTo()).thenReturn(testAmountTo);
		when(incomingTrade.getUser()).thenReturn(user);
		when(tradeInDb.getAmountFrom()).thenReturn(testAmountFrom);
		when(tradeInDb.getUser()).thenReturn(user);
		
		Optional<Trade> retTrade = transactions.transact(tradeInDb, incomingTrade);
		
		// Assert
		assertEquals(present, retTrade.isPresent());
		assertEquals(testIncomingTradeId, retTrade.get().getTradeId());
		
	}
	
	//This tests that when the amount difference are exactly the same for the first 6d.p.
	@Test
	@DisplayName("Make a transaction whose trade amounts are not exact due to non-exact amount when matched trade is higher")
	void testMakeTradePartialAmountWhenMatchedTradeIshigher() {
		
		// Arrange
		// act
		boolean present = true;
		long testUserId = 500;
		long testIncomingTradeId = 1000;
		long testMatchTradeId = 5000;
		
		double testAmountTo = 200.00;
		double testAmountFrom = 1000.000000;
		when(user.getUserId()).thenReturn(testUserId);
		when(incomingTrade.getTradeId()).thenReturn(testIncomingTradeId);
		when(incomingTrade.getAmountTo()).thenReturn(testAmountTo);
		when(incomingTrade.getUser()).thenReturn(user);
		when(tradeInDb.getTradeId()).thenReturn(testMatchTradeId);
		when(tradeInDb.getAmountFrom()).thenReturn(testAmountFrom);
		when(tradeInDb.getUser()).thenReturn(user);
		
		Optional<Trade> retTrade = transactions.transact(tradeInDb, incomingTrade);
		
		// Assert
		assertEquals(present, retTrade.isPresent());
		assertEquals(testMatchTradeId, retTrade.get().getTradeId());
		
	}
//
//	@Test
//	@DisplayName("Save trade when a trade DOESNT exists in database")
//	void testSaveATradeThatDoesntExists() {
//
//		// Arrange
//		// act
//		long testTradeId = 1000;
//		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
//		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.empty());
//		Trade retTrade = tradeService.save(incomingTrade);
//
//		// Assert
//		verify(tradeRepo).save(incomingTrade);
//		assertEquals(testTradeId, retTrade.getTradeId());
//
//	}
//	
//	@Test
//	@DisplayName("Retreieve single trades by ID that is in the database")
//	void testRetreieveSingleTrades() {
//
//		// Arrange
//		// act
//		long testTradeId = 1000;
//		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
//		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.ofNullable(incomingTrade));
//
//		Trade retTrade = tradeService.find(incomingTrade);
//		assertEquals(testTradeId, retTrade.getTradeId());
//		
//	}
//
//	@Test
//	@DisplayName("Retreieve single trades by ID that is NOT in the database")
//	void testRetreieveSingleTradesButNoInDatabase() {
//
//		// Arrange
//		// act
//		long testTradeId = 1000;
//		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
//		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.empty());
//
//		// Assert
//		assertThrows(TradeNotFoundException.class, () -> {
//
//			// Code that should throw TradeNotFoundException
//			Trade retTrade = tradeService.find(incomingTrade);
//
//		}, "no such trade");
//		
//	}
//	
//	@Test
//	@DisplayName("Remove single trades by ID that is in the database that fails")
//	void testRemoveSingleTradeInDBUnsuccessful() {
//		
//		// Arrange
//		// act
//		long testTradeId = 1000;
//		boolean empty = false;
//		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
//		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.ofNullable(incomingTrade));
//		boolean retResult = tradeService.remove(incomingTrade);
//		
//		// Assert
//		assertEquals(empty, retResult);
//		verify(transactions).refund(any());
//		
//	}
//	
//	@Test
//	@DisplayName("Remove single trades by ID that is in the database that passes")
//	void testRemoveSingleTradeInDBButSuccessful() {
//		
//		// Arrange
//		// act
//		long testTradeId = 1000;
//		boolean empty = true;
//		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
//		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.empty());
//		boolean retResult = tradeService.remove(incomingTrade);
//		
//		// Assert
//		assertEquals(empty, retResult);
//		verify(transactions).refund(any());
//		
//	}
//	
//	@Test
//	@DisplayName("Find trade that matches but it IS NOT successful")
//	void testFindMatchesUnsuccessfully() {
//		
//		// Arrange
//		// act
//		boolean empty = true;
//		List<Optional<Trade>> matches = new ArrayList<Optional<Trade>>();
//		
//		//set price due to division operation in trade service 
//		when(incomingTrade.getPrice()).thenReturn(2.00);
//		
//		//return empty list as no matches
//		when(tradeRepo.findMatchTradeIdenCurrencyAndTypeAndPrice(anyDouble(),anyString(),anyString())).thenReturn(matches);
//		
//		Optional<Trade> retResult = tradeService.findMatches(incomingTrade);
//		
//		// Assert
//		assertEquals(empty , retResult.isEmpty());
//		
//	}
//
//	@Test
//	@DisplayName("Find trade that matches but it IS successful")
//	void testFindMatchesSuccessfully() {
//		
//		// Arrange
//		// act
//		long testTradeId = 1000;
//		List<Optional<Trade>> matches = new ArrayList<Optional<Trade>>();
//		when(tradeInDb.getTradeId()).thenReturn(testTradeId);
//		matches.add(Optional.ofNullable(tradeInDb));
//		System.out.println(matches);
//		System.out.println(matches.get(0));
//		
//		//set price due to division operation in trade service 
//		when(incomingTrade.getPrice()).thenReturn(2.00);
//		//return empty list as no matches
//		when(tradeRepo.findMatchTradeIdenCurrencyAndTypeAndPrice(anyDouble(),anyString(),anyString())).thenReturn(matches);
//		Optional<Trade> retResult = tradeService.findMatches(incomingTrade);
//		
//		// Assert
//		assertEquals(tradeInDb.getTradeId(), retResult.get().getTradeId());
//		
//	}
}
