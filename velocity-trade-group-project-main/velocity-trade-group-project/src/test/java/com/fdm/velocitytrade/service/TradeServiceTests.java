package com.fdm.velocitytrade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
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

import com.fdm.velocitytrade.exceptions.TradeExistsException;
import com.fdm.velocitytrade.exceptions.TradeNotFoundException;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.repo.TradeRepository;
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
class TradeServiceTests {

	@Autowired
	TradeService tradeService;

	@MockBean
	TradeRepository tradeRepo;

	@MockBean
	Transactions transactions;

	
	@Mock
	Trade tradeInDb;

	@Mock
	Trade incomingTrade;

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
	@DisplayName("Save trade when a trade ALREADY exists in database")
	void testSaveATradeThatExists() {

		// Arrange
		// act
		long testTradeId = 1000;
		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.ofNullable(incomingTrade));

		// Assert
		assertThrows(TradeExistsException.class, () -> {

			// Code that should throw TradeExistsException
			Trade retTrade = tradeService.save(incomingTrade);

		}, "no such trade");

	}

	@Test
	@DisplayName("Save trade when a trade DOESNT exists in database")
	void testSaveATradeThatDoesntExists() {

		// Arrange
		// act
		long testTradeId = 1000;
		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.empty());
		Trade retTrade = tradeService.save(incomingTrade);

		// Assert
		verify(tradeRepo).save(incomingTrade);
		assertEquals(testTradeId, retTrade.getTradeId());

	}
	
	@Test
	@DisplayName("Retreieve single trades by ID that is in the database")
	void testRetreieveSingleTrades() {

		// Arrange
		// act
		long testTradeId = 1000;
		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.ofNullable(incomingTrade));

		Trade retTrade = tradeService.find(incomingTrade);
		assertEquals(testTradeId, retTrade.getTradeId());
		
	}

	@Test
	@DisplayName("Retreieve single trades by ID that is NOT in the database")
	void testRetreieveSingleTradesButNoInDatabase() {

		// Arrange
		// act
		long testTradeId = 1000;
		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.empty());

		// Assert
		assertThrows(TradeNotFoundException.class, () -> {

			// Code that should throw TradeNotFoundException
			Trade retTrade = tradeService.find(incomingTrade);

		}, "no such trade");
		
	}
	
	@Test
	@DisplayName("Remove single trades by ID that is in the database that fails")
	void testRemoveSingleTradeInDBUnsuccessful() {
		
		// Arrange
		// act
		long testTradeId = 1000;
		boolean empty = false;
		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.ofNullable(incomingTrade));
		boolean retResult = tradeService.remove(incomingTrade);
		
		// Assert
		assertEquals(empty, retResult);
		verify(transactions).refund(any());
		
	}
	
	@Test
	@DisplayName("Remove single trades by ID that is in the database that passes")
	void testRemoveSingleTradeInDBButSuccessful() {
		
		// Arrange
		// act
		long testTradeId = 1000;
		boolean empty = true;
		when(incomingTrade.getTradeId()).thenReturn(testTradeId);
		when(tradeRepo.findById(incomingTrade.getTradeId())).thenReturn(Optional.empty());
		boolean retResult = tradeService.remove(incomingTrade);
		
		// Assert
		assertEquals(empty, retResult);
		verify(transactions).refund(any());
		
	}
	
	@Test
	@DisplayName("Find trade that matches but it IS NOT successful")
	void testFindMatchesUnsuccessfully() {
		
		// Arrange
		// act
		boolean empty = true;
		List<Optional<Trade>> matches = new ArrayList<Optional<Trade>>();
		
		//set price due to division operation in trade service 
		when(incomingTrade.getPrice()).thenReturn(2.00);
		
		//return empty list as no matches
		when(tradeRepo.findMatchTradeIdenCurrencyAndTypeAndPrice(anyDouble(),anyString(),anyString())).thenReturn(matches);
		
		Optional<Trade> retResult = tradeService.findMatches(incomingTrade);
		
		// Assert
		assertEquals(empty , retResult.isEmpty());
		
	}

	@Test
	@DisplayName("Find trade that matches but it IS successful")
	void testFindMatchesSuccessfully() {
		
		// Arrange
		// act
		long testTradeId = 1000;
		List<Optional<Trade>> matches = new ArrayList<Optional<Trade>>();
		when(tradeInDb.getTradeId()).thenReturn(testTradeId);
		matches.add(Optional.ofNullable(tradeInDb));
		System.out.println(matches);
		System.out.println(matches.get(0));
		
		//set price due to division operation in trade service 
		when(incomingTrade.getPrice()).thenReturn(2.00);
		//return empty list as no matches
		when(tradeRepo.findMatchTradeIdenCurrencyAndTypeAndPrice(anyDouble(),anyString(),anyString())).thenReturn(matches);
		Optional<Trade> retResult = tradeService.findMatches(incomingTrade);
		
		// Assert
		assertEquals(tradeInDb.getTradeId(), retResult.get().getTradeId());
		
	}
}
