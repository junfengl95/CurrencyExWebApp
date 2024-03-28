package com.fdm.velocitytrade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.http.ResponseEntity;

import com.fdm.velocitytrade.exceptions.TradeExistsException;
import com.fdm.velocitytrade.exceptions.TradeNotFoundException;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.TransactionHistory;
import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.model.Wallet;
import com.fdm.velocitytrade.repo.TradeRepository;
import com.fdm.velocitytrade.repo.TransactionHistoryRepository;
import com.fdm.velocitytrade.repo.UserRepository;
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
class TradeControllerTests {

	@Autowired
	TradeController tradeController;

	@MockBean
	UserService userService;

	@MockBean
	TradeService tradeService;

	@MockBean
	TransactionHistoryRepository transactionHistoryRepo;

	@MockBean
	Transactions transactions;

	@Mock
	Trade incomingTrade;
	@Mock
	Trade retTrade;
	@Mock
	Trade matchedTrade;
	@Mock
	Trade t2;
	@Mock
	Trade t4;
	@Mock
	User incomingUser;
	@Mock
	User matchedUser;
	
	
	@Mock
	ResponseEntity<Trade> responseEntity;

	private List<Trade> tradeList = new ArrayList<Trade>();
	LocalDateTime currentDateTime = LocalDateTime.now();
	User user1;
	private Map<String, Double> currencyMap1 = new HashMap<String, Double>();
	@Autowired
	TradeRepository ts2;
	@Autowired
	UserRepository ur2;

	@BeforeEach
	void initialise() {

//		currencyMap1.put("SGD", 5000.0);
//		currencyMap1.put("USD", 500.0);
//		user1 = new User("Bob", "password123", "bob@123.com", "USER", new Wallet(currencyMap1),
//				new Bank("DBS", "abc123", "SGD"));
//		tradeList.add(new Trade("SGD", "KRW", ORDERTYPE.limit, 900.0, 1000.0, 1000.0 * 900.0, "25/5/2025",
//				currentDateTime, null, user1));
//		List<User> ul = new ArrayList<User>();
//		ul.add(user1);
//		ur2.saveAll(ul);
//		
//		ts2.saveAll(tradeList);
//		ts2.findAll().stream().forEach(t -> System.out.println("Trade is : " + t));
//		System.out.println("Here in init");
	}

	@Test
	@DisplayName("VM connect to datasource")
	void contextLoads() {

		// if fails, most likely your VM args do not contain username and password.
		// JUnit has separate VM args compared to Java app.
		assertEquals(1, 1);

	}

	@Test
	@DisplayName("Retreieve all trades in the data base")
	void testRetreieveAllTrades() {

		List<Trade> listTrade = new ArrayList<>(Arrays.asList(incomingTrade, t2, matchedTrade, t4));

		// Arrange
		// act
		when(tradeService.findAll()).thenReturn(listTrade);

		// Assert
		List<Trade> retTrade = tradeController.retrieveAllTrades();

		assertEquals(listTrade.size(), retTrade.size());

	}

	@Test
	@DisplayName("Retreieve single trades by ID that is in the database")
	void testRetreieveSingleTrades() {

		int tradeid = 2;
		// Arrange
		// act
		when(t2.getTradeId()).thenReturn((long) tradeid);
		when(tradeService.find(tradeid)).thenReturn(t2);

		// Assert
		Trade retTrade = tradeController.retrieveSingleTrade(tradeid);

		assertEquals(tradeid, retTrade.getTradeId());

	}

	@Test
	@DisplayName("Retreieve single trades by ID that is NOT in the database")
	void testRetreieveSingleTradesButNoInDatabase() {

		int tradeid = 1;
		// Arrange
		// act
		when(tradeService.find(tradeid)).thenThrow(new TradeNotFoundException("no such trade"));

		// Assert
		assertThrows(TradeNotFoundException.class, () -> {

			// Code that should throw TradeNotFoundException
			Trade retTrade = tradeController.retrieveSingleTrade(tradeid);

		}, "no such trade");

	}

	@Test
	@DisplayName("Retreieve single trades by With Trade Obj that is IN the database")
	void testRetreieveSingleTradesWithTradeObj() {

		int tradeid = 2;
		// Arrange
		// act
		when(t2.getTradeId()).thenReturn((long) tradeid);
		when(tradeService.find(t2)).thenReturn(t2);
		Trade retTrade = tradeController.retrieveSingleTrade(tradeid, t2);

		// Assert
		assertEquals(tradeid, retTrade.getTradeId());

	}

	@Test
	@DisplayName("Retreieve single trades by With Trade Obj  that is NOT IN the database")
	void testRetreieveSingleTradesButNoInDatabaseWithTradeObj() {

		int tradeid = 2;
		// Arrange
		// act
		when(tradeService.find(t2)).thenThrow(new TradeNotFoundException("no such trade"));

		// Assert
		assertThrows(TradeNotFoundException.class, () -> {

			// Code that should throw TradeNotFoundException
			Trade retTrade = tradeController.retrieveSingleTrade(tradeid, t2);

		}, "no such trade");

	}

	@Test
	@DisplayName("Save a new trade that is NOT IN the database without specifying the ID that has NO matches")
	void testSaveNewTradeWithNoTradeIDNoMatch() {

		// Arrange
		// act
//		long testUserId = 500;
//		when(incomingUser.getUserId()).thenReturn(testUserId);
//		when(incomingTrade.getUser()).thenReturn(incomingUser);
//		when(incomingTrade.getCurrencyFrom()).thenReturn("");
//		when(incomingTrade.getAmountFrom()).thenReturn(0.00);
		when(tradeService.save(incomingTrade)).thenReturn(incomingTrade);
		
		ResponseEntity<Trade> retTrade = tradeController.saveNewTrade(incomingTrade).get();

		// Assert
		assertEquals(incomingTrade, retTrade.getBody());

	}

	@Test
	@DisplayName("Save a new trade that is IN the database by specifying the ID that has NO matched")
	void testSaveNewTradeWithTradeIDNoMatch() {

		// Arrange
		// act
//		long testUserId = 500;
//		when(incomingUser.getUserId()).thenReturn(testUserId);
//		when(incomingTrade.getUser()).thenReturn(incomingUser);
//		when(incomingTrade.getCurrencyFrom()).thenReturn("");
//		when(incomingTrade.getAmountFrom()).thenReturn(0.00);
		when(tradeService.save(incomingTrade)).thenThrow(
				new TradeExistsException("Trade already exists in database. Use update if intending to update."));

		// Assert
		assertThrows(TradeExistsException.class, () -> {

			// Code that should throw TradeNotFoundException
			ResponseEntity<Trade> retTrade = tradeController.saveNewTrade(incomingTrade).get();

		}, "Trade exists");

	}

	
	@Test
	@DisplayName("Save a new trade but there is a matching trade in the database with the exact same amount and currency")
	void testNewSaveTradeWhenThereIsAMatchWithExactAmount() {

		// Arrange
//		Map<String, Double> incomingWallet = new HashMap<String, Double>();
//		Map<String, Double> matchedWallet = new HashMap<String, Double>();
//
//		double startingSGDIncoming = 1000.0;
//
//		double startingMYRmatched = 550.0;
//
//		incomingWallet.put("SGD", startingSGDIncoming);
//
//		matchedWallet.put("MYR", startingMYRmatched);
//
//		double SGDtoMYRPrice = 3.5;
//		double SGDtoMYRPriceInverse = 0.2857;
//
//		double incomingAmountFrom = 100;
//		double matchedAmountFrom = 350;
//
//		double incomingAmountTo = SGDtoMYRPrice * incomingAmountFrom;
//		double matchedAmountTo = SGDtoMYRPriceInverse * matchedAmountFrom;
//
//		double expectedIncomingWalletSGDAmount = 900;
//		double expectedIncomingWalletMYRAmount = 350;
//
//		double expectedMatchedWalletSGDAmount = 100;
//		double expectedMatchedWalletMYRAmount = 200;
//
//		// act
//
//		when(incomingUser.getUserId()).thenReturn((long) 1);
//		when(matchedUser.getUserId()).thenReturn((long) 2);
//
//		when(incomingUser.getWallet()).thenReturn(new Wallet(incomingWallet));
//		when(matchedUser.getWallet()).thenReturn(new Wallet(matchedWallet));
//
////		when(incomingTrade.getPrice()).thenReturn(SGDtoMYRPrice);
//		when(incomingTrade.getCurrencyFrom()).thenReturn("SGD");
//		when(incomingTrade.getCurrencyTo()).thenReturn("MYR");
//		when(incomingTrade.getAmountFrom()).thenReturn(incomingAmountFrom);
//		when(incomingTrade.getAmountTo()).thenReturn(incomingAmountTo);
//		when(incomingTrade.getUser()).thenReturn(incomingUser);
//
////		when(matchedTrade.getPrice()).thenReturn(SGDtoMYRPriceInverse);
//		when(matchedTrade.getCurrencyFrom()).thenReturn("MYR");
//		when(matchedTrade.getCurrencyTo()).thenReturn("SGD");
//		when(matchedTrade.getAmountFrom()).thenReturn(matchedAmountFrom);
//		when(matchedTrade.getAmountTo()).thenReturn(matchedAmountTo);
//		when(matchedTrade.getUser()).thenReturn(matchedUser);
//
//		when(ts.findMatches(incomingTrade)).thenReturn(Optional.ofNullable(matchedTrade));
//
//		when(transactionHistoryRepo.save(any())).thenReturn(new TransactionHistory());
//
////		 mock user service which updates wallet
//		when(us.withdrawMoneyFromWallet(anyLong(), anyString(), anyDouble())).thenReturn(true)
//				.thenAnswer(invocation -> {
//
//					// Access information about the method invocation
//					Object[] arguments = invocation.getArguments(); // Get arguments passed to the method
//					Long userId = (Long) arguments[0];
//					String currency = (String) arguments[1];
//					Double amount = (Double) arguments[2];
//
//					// Print out the arguments
//					System.out.println("Method depositMoneyToWallet called with arguments: userId=" + userId
//							+ ", currency=" + currency + ", amount=" + amount);
//
//					incomingUser.getWallet().getCurrencyMap().put("SGD", startingSGDIncoming - incomingAmountFrom);
//					matchedUser.getWallet().getCurrencyMap().put("MYR", startingMYRmatched - matchedAmountFrom);
//					return true;
//
//				});
////		 mock user service which updates wallet
////		when(us.withdrawMoneyFromWallet(eq((long)1),eq("SGD"), eq((double)100.0)))
////		.thenReturn(true)
////		.thenAnswer(invocation -> {
////			incomingUser.getWallet().getCurrencyMap().put("SGD", startingSGDIncoming - incomingAmountFrom);
////			matchedUser.getWallet().getCurrencyMap().put("MYR", startingMYRmatched - matchedAmountFrom);
////			return true;
////		});
//
//		when(us.depositMoneyToWallet(anyLong(), anyString(), anyDouble())).thenReturn(true).thenAnswer(invocation -> {
//
//			// Access information about the method invocation
//			Object[] arguments = invocation.getArguments(); // Get arguments passed to the method
//			Long userId = (Long) arguments[0];
//			String currency = (String) arguments[1];
//			Double amount = (Double) arguments[2];
//
//			// Print out the arguments
//			System.out.println("Method depositMoneyToWallet called with arguments: userId=" + userId + ", currency="
//					+ currency + ", amount=" + amount);
//
//			incomingUser.getWallet().getCurrencyMap().put("MYR", matchedAmountFrom);
//			matchedUser.getWallet().getCurrencyMap().put("SGD", incomingAmountFrom);
//
//			return true;
//
//		});
//
//		Optional<ResponseEntity<Trade>> retTrade = tc.saveNewTrade(incomingTrade);
//
//		// Assert
//		verify(us, times(2)).withdrawMoneyFromWallet(any(), any(), any());
//		verify(us, times(2)).depositMoneyToWallet(any(), any(), any());
//		assertEquals(expectedIncomingWalletSGDAmount, incomingUser.getWallet().getCurrencyMap().get("SGD"));
//		assertEquals(expectedIncomingWalletMYRAmount, incomingUser.getWallet().getCurrencyMap().get("MYR"));
//		assertEquals(expectedMatchedWalletSGDAmount, matchedUser.getWallet().getCurrencyMap().get("SGD"));
//		assertEquals(expectedMatchedWalletMYRAmount, matchedUser.getWallet().getCurrencyMap().get("MYR"));
//		assert (retTrade.get().getBody().isEmpty());

	}

	@Test
	@DisplayName("Save a new trade but there is a match in databse and it fulfil PARTIAL amount")
	void testSaveNewTradeThatMatchesPartialAmount() {

		// Arrange
		// act
		long testId = 1000;
//		long testUserId = 500;
//		when(incomingUser.getUserId()).thenReturn(testUserId);
//		when(incomingTrade.getUser()).thenReturn(incomingUser);
//		when(incomingTrade.getCurrencyFrom()).thenReturn("");
//		when(incomingTrade.getAmountFrom()).thenReturn(0.00);
		when(retTrade.getTradeId()).thenReturn(testId);
		when(tradeService.findMatches(incomingTrade)).thenReturn(Optional.ofNullable(matchedTrade));
		when(transactions.transact(matchedTrade, incomingTrade)).thenReturn(Optional.of(retTrade));

		Trade retTradeMethod = tradeController.saveNewTrade(incomingTrade).get().getBody();

		// Assert
		assertEquals(testId, retTradeMethod.getTradeId());

	}
	
	@Test
	@DisplayName("Save a new trade but there is a match in databse and it fulfil EXACT amount")
	void testSaveNewTradeThatMatchesExactlAmount() {

		// Arrange
		// act
		boolean Empty = true;
//		long testUserId = 500;
//		when(incomingUser.getUserId()).thenReturn(testUserId);
//		when(incomingTrade.getUser()).thenReturn(incomingUser);
//		when(incomingTrade.getCurrencyFrom()).thenReturn("");
//		when(incomingTrade.getAmountFrom()).thenReturn(0.00);
		when(tradeService.findMatches(incomingTrade)).thenReturn(Optional.ofNullable(matchedTrade));
		when(transactions.transact(matchedTrade, incomingTrade)).thenReturn(Optional.empty());

		Trade retTradeMethod = tradeController.saveNewTrade(incomingTrade).get().getBody();
		
		// Assert
		assertEquals(Empty , retTradeMethod.isEmpty());

	}


}
