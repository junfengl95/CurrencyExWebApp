package com.fdm.velocitytrade.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.User;
//import com.fdm.velocitytrade.model.WalletCurrency;
import com.fdm.velocitytrade.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping
	public List<User> retrieveAllUsers() {

		return userService.findAllUsers();
	}

	@GetMapping("/{id}")
	public User retrieveSingleUser(@PathVariable("id") Long userId) {

		return userService.findUserById(userId);
	}

	@PostMapping("/register")
	public ResponseEntity<User> createNewUser(@RequestBody User user) {
		// User registration logic
		User savedUser = userService.registerNewUser(user.getUsername(), user.getPassword(), user.getEmail());

		// Build the URI for the newly created user's location
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedUser.getUserId()).toUri();

		// Return a response entity with status code 201 Created
		return ResponseEntity.created(location).body(savedUser);
	}

	@GetMapping("/username/{username}")
	public Long retrieveUserIdByUsername(@PathVariable("username") String username) {

		return userService.findUserIdByUsername(username);
	}

	@GetMapping("/{id}/holdingCurrencies")
	public Map<String, Double> retrieveHoldingCurrenciesByUsername(@PathVariable("id") Long usrid) {
		return userService.getHoldingCurrencyByUserId(usrid);
	}

	@PostMapping("/{id}/withdrawMoney/{amount}")
	public ResponseEntity<String> withdrawMoneyFromBank(@PathVariable("id") Long userid,
			@PathVariable("amount") Double amount) {
		if (userService.withdrawMoneyFromBank(userid, amount))
			return ResponseEntity.ok("Successsfully withdraw money from bank.");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Error: Failed to withdraw money from bank because of the invalid amount.");

	}

	@PostMapping("/{id}/depositMoney/{amount}")
	public ResponseEntity<String> depositMoneyToBank(@PathVariable("id") Long userid,
			@PathVariable("amount") Double amount) {
		if (userService.depositMoneyToBank(userid, amount))
			return ResponseEntity.ok("Successsfully deposit money to bank.");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				"Error: Failed to deposit money to bank because the wallet does not contain enough money or invalid amount.");
	}

	@PostMapping("/{id}/payForTrade/{currencyType}/{amount}")
	public ResponseEntity<String> withdrawMoneyFromWallet(@PathVariable("id") Long userid,
			@PathVariable("currencyType") String type, @PathVariable("amount") Double amount) {

		if (userService.withdrawMoneyFromWallet(userid, type, amount))
			return ResponseEntity.ok("Successsfully withdraw money from wallet.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				"Error: Failed to withdraw money from wallet because the wallet does not contain enough money or invalid amount.");
	}

	@PostMapping("/{id}/earnFromTrade/{currencyType}/{amount}")
	public ResponseEntity<String> depositMoneyToWallet(@PathVariable("id") Long userid,
			@PathVariable("currencyType") String type, @PathVariable("amount") Double amount) {

		if (userService.depositMoneyToWallet(userid, type, amount))
			return ResponseEntity.ok("Successsfully deposit money to wallet.");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Error: Failed to deposit money to wallet because of the invalid amount.");
	}

	@GetMapping("/getUser/{username}")
	public User retrieveUserByUsername(@PathVariable("username") String name) {
		return userService.findUserByUsername(name);
	}

	@PutMapping("/{id}/changeUsername/{username}")
	public User changeUsername(@PathVariable("id") Long userId, @PathVariable("username") String name) {
		return userService.updateUsername(userId, name);
	}

	@PutMapping("/{id}/changeUserPassword/{new_password}")
	public User changeUserPassword(@PathVariable("id") Long userId, @PathVariable("new_password") String password) {
		return userService.updatePassword(userId, password);
	}

	@PutMapping("/{id}/changeUserEmail/{new_email}")
	public User changeUserEmail(@PathVariable("id") Long userId, @PathVariable("new_email") String email) {
		return userService.updateEmail(userId, email);
	}

	@GetMapping("/getTrades/id/{id}")
	public List<Trade> getTradesByUserId(@PathVariable("id") Long id) {
		return userService.getAllTradesByUserId(id);
	}

	@GetMapping("/getTrades/name/{username}")
	public List<Trade> getTradesByUsername(@PathVariable("username") String name) {
		return userService.getAllTradesByUsername(name);
	}
	
	@GetMapping("/getPendingTrades/id/{id}")		// return the List of Trade that is Pending
	public List<Trade> getPendingTradesByUserId(@PathVariable("id") Long userId) {
		return userService.getAllPendingTradeByUserId(userId);
	}
	
	@GetMapping("/getPendingTrades/name/{username}")
	public List<Trade> getPendingTradesByUsername(@PathVariable("username") String name) {
		return userService.getAllPendingTradeByUsername(name);
	}
	
	@GetMapping("/getMatchedForwardTrades/id/{id}")		// return the List of Trade that is forwardMatchedPendingSettlement for Forward trade
	public List<Trade> getMatchedForwardTradesByUserId(@PathVariable("id") Long userId) {
		return userService.getAllMatchedForwardByUserId(userId);
	}
	
	@GetMapping("/getMatchedForwardTrades/name/{username}")
	public List<Trade> getMatchedForwardTradesByUsername(@PathVariable("username") String name) {
		return userService.getAllMatchedForwardByUsername(name);
	}
}
