package com.fdm.velocitytrade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fdm.velocitytrade.model.Bank;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.model.Wallet;
import com.fdm.velocitytrade.service.UserService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@Autowired
	UserController uc;
	
	@MockBean
	UserService us;
	
	@Mock
	User u1;
	
	@Mock
	User u2;
	
	@Mock
	Trade t1;
	
	@Mock
	Trade t2;
	
	
	@Test
	@DisplayName("VM connect to datasource")		// copy from the TradeController to test the connection to DB
	void contextLoads() {

		// if fails, most likely your VM args do not contain username and password.
		// JUnit has separate VM args compared to Java app.
		assertEquals(1, 1);

	}
	
	@Test
	@DisplayName("Test for register a new User")
	void TestRegisterUser() {
		u1 = new User("Test", "test123", "test@123.com", "USER", new Wallet(),
				new Bank("DBS", "abc123", "SGD"));
		
		when(us.registerUser(u1)).thenReturn(u1);
		
		ResponseEntity<User> response = uc.createNewUser(u1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(u1, response.getBody());
	}
	
	@Test
	@DisplayName("Test for retrieve all users")
	void TestRetrieveAllUsers() {
		List<User> users = Arrays.asList(
                new User("John", "john123", "john@123.com", "USER", new Wallet(), new Bank("DBS", "abc123", "SGD")),
                new User("Jane", "jane123", "jane@123.com", "USER", new Wallet(), new Bank("DBS", "abc123", "SGD"))
        );
		
		when(us.findAllUsers()).thenReturn(users);
		
		List<User> actual = uc.retrieveAllUsers();
		
		assertEquals(users.size(), actual.size());
		assertEquals(users.get(0), actual.get(0));
		assertEquals(users.get(1), actual.get(1));
	}
	
	@Test
	@DisplayName("Test for retrieve a single user")
	void TestRetrieveSingleUser() {
		Long userId = 1L;
	    User user = new User("John", "john123", "john@123.com", "USER", new Wallet(), new Bank("DBS", "abc123", "SGD"));

	    when(us.findUserById(userId)).thenReturn(user);

	    User retrievedUser = uc.retrieveSingleUser(userId);

	    assertEquals(user, retrievedUser);
	}
	
	@Test
	@DisplayName("Test for retrieving User ID by username")
	void TestRetrieveUserIdByUsername() {
	    String username = "john123";
	    Long userId = 1L;

	    when(us.findUserIdByUsername(username)).thenReturn(userId);

	    Long retrievedUserId = uc.retrieveUserIdByUsername(username);

	    assertEquals(userId, retrievedUserId);
	}
	
	@Test
	@DisplayName("Test for retrieving holding currencies by user ID")
	void TestRetrieveHoldingCurrenciesByUsername() {
	    Long userId = 1L;
	    Map<String, Double> holdingCurrencies = new HashMap<>();
	    holdingCurrencies.put("USD", 100.0);
	    holdingCurrencies.put("EUR", 200.0);

	    when(us.getHoldingCurrencyByUserId(userId)).thenReturn(holdingCurrencies);

	    Map<String, Double> retrievedHoldingCurrencies = uc.retrieveHoldingCurrenciesByUsername(userId);

	    assertEquals(holdingCurrencies.size(), retrievedHoldingCurrencies.size());
	    assertEquals(holdingCurrencies.get("USD"), retrievedHoldingCurrencies.get("USD"));
	    assertEquals(holdingCurrencies.get("EUR"), retrievedHoldingCurrencies.get("EUR"));
	}
	
	@Test
	@DisplayName("Test for withdrawing money from bank")
	void TestWithdrawMoneyFromBank() {
	    Long userId = 1L;
	    Double amount = 50.0;

	    when(us.withdrawMoneyFromBank(userId, amount)).thenReturn(true);

	    ResponseEntity<String> response = uc.withdrawMoneyFromBank(userId, amount);

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals("Successsfully withdraw money from bank.", response.getBody());
	}
	
	@Test
	@DisplayName("Test for withdrawing money from bank with invalid amount")
	void TestWithdrawMoneyFromBank_InvalidAmount() {
	    Long userId = 1L;
	    Double amount = -50.0;

	    when(us.withdrawMoneyFromBank(userId, amount)).thenReturn(false);

	    ResponseEntity<String> response = uc.withdrawMoneyFromBank(userId, amount);

	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	    assertEquals("Error: Failed to withdraw money from bank because of the invalid amount.", response.getBody());
	}
	
	@Test
	@DisplayName("Test for depositing money to bank")
	void TestDepositMoneyToBank() {
	    Long userId = 1L;
	    Double amount = 100.0;

	    when(us.depositMoneyToBank(userId, amount)).thenReturn(true);

	    ResponseEntity<String> response = uc.depositMoneyToBank(userId, amount);

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals("Successsfully deposit money to bank.", response.getBody());
	}

	@Test
	@DisplayName("Test for depositing money to bank with insufficient wallet balance")
	void TestDepositMoneyToBank_InsufficientBalance() {
	    Long userId = 1L;
	    Double amount = 100.0;

	    when(us.depositMoneyToBank(userId, amount)).thenReturn(false);

	    ResponseEntity<String> response = uc.depositMoneyToBank(userId, amount);

	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	    assertEquals("Error: Failed to deposit money to bank because the wallet does not contain enough money or invalid amount.", response.getBody());
	}
	
	@Test
	@DisplayName("Test for withdraw money from wallat")
	void TestWithdrawMoneyFromWallet() {
		Long userId = 1L;
		String currencyType = "USD";
		Double amount = 100.0;
		
		when(us.withdrawMoneyFromWallet(userId,currencyType, amount)).thenReturn(true);
		
		ResponseEntity<String> response = uc.withdrawMoneyFromWallet(userId,currencyType,amount);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals("Successsfully withdraw money from wallet.", response.getBody());
	
	}
	
	@Test
	@DisplayName("Test for withdrawing money from wallet with invalid amount")
	void TestWithdrawMoneyFromWallet_InvalidAmount() {
	    Long userId = 1L;
	    String currencyType = "USD";
	    Double amount = -50.0;

	    when(us.withdrawMoneyFromWallet(userId, currencyType, amount)).thenReturn(false);

	    ResponseEntity<String> response = uc.withdrawMoneyFromWallet(userId, currencyType, amount);

	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	    assertEquals("Error: Failed to withdraw money from wallet because the wallet does not contain enough money or invalid amount.", response.getBody());
	}
	
	@Test
	@DisplayName("Test for depositing money to wallet")
	void TestDepositMoneyFromWallet() {
	    Long userId = 1L;
	    String currencyType = "USD";
	    Double amount = 100.0;

	    when(us.depositMoneyToWallet(userId, currencyType, amount)).thenReturn(true);

	    ResponseEntity<String> response = uc.depositMoneyToWallet(userId, currencyType, amount);

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals("Successsfully deposit money to wallet.", response.getBody());
	}
	
	@Test
	@DisplayName("Test for depositing money to wallet with invalid amount")
	void TestDepositMoneyFromWallet_InvalidAmount() {
	    Long userId = 1L;
	    String currencyType = "USD";
	    Double amount = -100.0;

	    when(us.depositMoneyToWallet(userId, currencyType, amount)).thenReturn(false);

	    ResponseEntity<String> response = uc.depositMoneyToWallet(userId, currencyType, amount);

	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	    assertEquals("Error: Failed to deposit money to wallet because of the invalid amount.", response.getBody());
	}
	
	@Test
	@DisplayName("Test for retrieving user by username")
	void TestRetrieveUserByUsername() {
	    String username = "john123";
	    User user = new User();
	    user.setUserId(1L);
	    user.setUsername(username);

	    when(us.findUserByUsername(username)).thenReturn(user);

	    User retrievedUser = uc.retrieveUserByUsername(username);

	    assertNotNull(retrievedUser);
	    assertEquals(user.getUserId(), retrievedUser.getUserId());
	    assertEquals(user.getUsername(), retrievedUser.getUsername());
	}
	
	@Test
	@DisplayName("Test for changing the user information")
	void TestChangeUserInformation() {
		Long userId = 1L;
		String newName = "Jack";
		String newPassword = "jack123";
		String newEmail = "jck@123.com";
		
		User updatedUser = new User();
	    updatedUser.setUserId(userId);
	    updatedUser.setUsername(newName);
	    updatedUser.setPassword(newPassword);
	    updatedUser.setEmail(newEmail);

	    when(us.updateUsername(userId, newName)).thenReturn(updatedUser);
	    when(us.updatePassword(userId, newPassword)).thenReturn(updatedUser);
	    when(us.updateEmail(userId, newEmail)).thenReturn(updatedUser);
	    
	    User changedUser;

	    // Test for changing the Name
	    changedUser = uc.changeUsername(userId, newName);
	    assertNotNull(changedUser);
	    assertEquals(updatedUser.getUserId(), changedUser.getUserId());
	    assertEquals(updatedUser.getUsername(), changedUser.getUsername());
	    
	 // Test changing password
	    changedUser = uc.changeUserPassword(userId, newPassword);
	    assertNotNull(changedUser);
	    assertEquals(updatedUser.getUserId(), changedUser.getUserId());
	    assertEquals(updatedUser.getPassword(), changedUser.getPassword());

	    // Test changing email
	    changedUser = uc.changeUserEmail(userId, newEmail);
	    assertNotNull(changedUser);
	    assertEquals(updatedUser.getUserId(), changedUser.getUserId());
	    assertEquals(updatedUser.getEmail(), changedUser.getEmail());
	}
	
	@Test
	@DisplayName("Test for getting trades by user ID")
	void TestGetTradesByUserId() {
	    Long userId = 1L;
	    List<Trade> trades = new ArrayList<>(Arrays.asList(t1,t2));

	    when(us.getAllTradesByUserId(userId)).thenReturn(trades);

	    List<Trade> retrievedTrades = uc.getTradesByUserId(userId);

	    assertNotNull(retrievedTrades);
	    assertEquals(trades.size(), retrievedTrades.size());
	}
	
	@Test
	@DisplayName("Test for getting trades by username")
	void TestGetTradesByUsername() {
	    String username = "john123";
	    List<Trade> trades = new ArrayList<>(Arrays.asList(t1,t2));

	    when(us.getAllTradesByUsername(username)).thenReturn(trades);

	    List<Trade> retrievedTrades = uc.getTradesByUsername(username);

	    assertNotNull(retrievedTrades);
	    assertEquals(trades.size(), retrievedTrades.size());
	}
}
