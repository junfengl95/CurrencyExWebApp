package com.fdm.velocitytrade.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fdm.velocitytrade.TRADESTATUS;
import com.fdm.velocitytrade.exceptions.DuplicateEmailException;
import com.fdm.velocitytrade.exceptions.DuplicateUsernameException;
import com.fdm.velocitytrade.exceptions.InsufficientFundsException;
import com.fdm.velocitytrade.exceptions.UserNotFoundException;
import com.fdm.velocitytrade.model.Bank;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.User;
import com.fdm.velocitytrade.model.Wallet;
//import com.fdm.velocitytrade.model.WalletCurrency;
import com.fdm.velocitytrade.repo.UserRepository;
import com.fdm.velocitytrade.repo.WalletRepository;

import jakarta.transaction.Transactional;

/**
 * Service class for managing users.
 * 
 * @author junfeng.lee
 * @version 0.02
 * @since 09/01/2024
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WalletRepository walletRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger LOGGER = LogManager.getLogger(UserService.class);

	public User registerUser(User user) {
		try {
			// Check if the username already exists
			if (userRepo.existsByUsername(user.getUsername())) {
				throw new DuplicateUsernameException(
						"Username " + user.getUsername() + " already in use. \n Please choose a different username.");
			}
			// Check if email already exists
			if (userRepo.existsByEmail(user.getEmail())) {
				throw new DuplicateEmailException(
						"Email " + user.getEmail() + " already in use. \n Please choose a different email.");
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			LOGGER.info("User registered successfully: {}", user.getUsername());

			return userRepo.save(user);
		} catch (DuplicateUsernameException | DuplicateEmailException e) {
			LOGGER.error("Error saving user: {}", e.getMessage(), e);
			throw e; // Rethrow the exception for the controller to handle
		}
	}

	public User registerNewUser(String newusername, String newpassword, String newemail) {

		User newreigstreduser = new User(newusername, newpassword, newemail, "USER",
				new Wallet(new HashMap<String, Double>()), new Bank("bankname", "accountnumber", "SGD"));
		try {
			// Check if the username already exists
			if (userRepo.existsByUsername(newreigstreduser.getUsername())) {
				throw new DuplicateUsernameException("Username " + newreigstreduser.getUsername()
						+ " already in use. \n Please choose a different username.");
			}
			// Check if email already exists
			if (userRepo.existsByEmail(newreigstreduser.getEmail())) {
				throw new DuplicateEmailException("Email " + newreigstreduser.getEmail()
						+ " already in use. \n Please choose a different email.");
			}

			newreigstreduser.setPassword(passwordEncoder.encode(newreigstreduser.getPassword()));

			LOGGER.info("User registered successfully: {}", newreigstreduser.getUsername());
			walletRepo.save(newreigstreduser.getWallet());
			return userRepo.save(newreigstreduser);
		} catch (DuplicateUsernameException | DuplicateEmailException e) {
			LOGGER.error("Error saving user: {}", e.getMessage(), e);
			throw e; // Rethrow the exception for the controller to handle
		}
	}

	public void deleteUserById(Long userId) {
		try {
			User managedUser = userRepo.findById(userId)
					.orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

			userRepo.delete(managedUser);
		} catch (UserNotFoundException e) {
			LOGGER.error("Error deleting User: {}", e.getMessage(), e);
		}
	}

	public User findUserById(Long userId) {
		try {
			User user = userRepo.findById(userId)
					.orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

			LOGGER.info("User with id {} found", userId);

			return user;

		} catch (UserNotFoundException e) {
			LOGGER.error("Error finding user with id {}: {}", userId, e.getMessage(), e);
			throw e;
		}
	}

	public List<User> findAllUsers() {
		try {
			LOGGER.info("Retrieved all users successfully");
			return userRepo.findAll();
		} catch (UserNotFoundException e) {
			LOGGER.error("Error retrieving all user list");
			throw e;
		}

	}

	public User updateUsername(Long userId, String newUsername) {
		try {
			User managedUser = userRepo.findById(userId)
					.orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
			// Check if newUsername already exist
			if (userRepo.existsByUsername(newUsername)) {
				throw new DuplicateUsernameException("Username " + managedUser.getUsername()
						+ " already in use. \n Please choose a different username.");
			} else {
				// Update the username if it is not a duplicate
				managedUser.setUsername(newUsername);
			}

			LOGGER.info("Username succussfully change to {}", newUsername);

			return userRepo.save(managedUser);
		} catch (UserNotFoundException | DuplicateUsernameException e) {
			LOGGER.error("Error updating username for User with id {}: {}", userId, e.getMessage(), e);
			throw e;
		}
	}

	public User updatePassword(Long userId, String newPassword) {
		try {
			User managedUser = userRepo.findById(userId)
					.orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

			// Validate the new password based on entity constraints
			managedUser.setPassword(newPassword);
			managedUser.setPassword(passwordEncoder.encode(managedUser.getPassword()));

			LOGGER.info("Password updated for User with id {}", userId);

			return userRepo.save(managedUser);
		} catch (UserNotFoundException e) {
			LOGGER.error("Error updating password for User with id {}: {}", userId, e.getMessage(), e);
			throw e;
		}
	}

	public User updateEmail(Long userId, String newEmail) {
		try {
			User managedUser = userRepo.findById(userId)
					.orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
			if (userRepo.existsByEmail(newEmail)) {
				throw new DuplicateEmailException("Email " + " already in use. \n Please choose a different email.");
			}
			managedUser.setEmail(newEmail);

			LOGGER.info("Email succesfully updated for User with id {}", userId);

			return userRepo.save(managedUser);
		} catch (UserNotFoundException | DuplicateEmailException e) {
			LOGGER.error("Error updating password for User with id {}: {}", userId, e.getMessage(), e);
			throw e;
		}
	}

	public Long findUserIdByUsername(String username) {
		try {
			User user = userRepo.findByUsername(username)
					.orElseThrow(() -> new UserNotFoundException("User {} " + username + " not found"));
			LOGGER.info("User: {} found", username);
			long userId = user.getUserId();
			return userId;
		} catch (UserNotFoundException e) {
			LOGGER.error("Error finding user with username {}: {}", username, e.getMessage(), e);
			throw e;
		}
	}

	public Map<String, Double> getHoldingCurrencyByUserId(Long userId) {
		User user = this.findUserById(userId);
		Wallet wallet = user.getWallet();
		return wallet.getCurrencyMap();
	}

	@Transactional
	public boolean withdrawMoneyFromBank(Long userId, Double amount) {
		// error handling for invalid amount
		if (amount < 0)
			return false;
		User u = this.findUserById(userId);
		String currencyType = u.getBank().getCurrencyname();

		// use the getHoldingCurrencyByUserId to get the Map<<String, Double> and find
		// is there the same currencyType in the map
		Map<String, Double> holding = this.getHoldingCurrencyByUserId(userId);

		if (holding.containsKey(currencyType)) {
			// update the Map and add the amount in the currently holding currency
			holding.put(currencyType, holding.get(currencyType) + amount);

			walletRepo.save(u.getWallet());
			userRepo.save(u);
		} else {
			holding.put(currencyType, amount);
			walletRepo.save(u.getWallet());
			userRepo.save(u);
		}

		return true;

	}

	@Transactional
	public boolean depositMoneyToBank(Long userId, Double amount) {
		// error handling for invalid amount
		if (amount < 0)
			return false;

		User u = this.findUserById(userId);
		String currencyType = u.getBank().getCurrencyname();

		// use the getHoldingCurrencyByUserId to get the Map<<String, Double> and find
		// is there the same currencyType in the map
		Map<String, Double> holding = this.getHoldingCurrencyByUserId(userId);

		if (holding.containsKey(currencyType) && holding.get(currencyType) >= amount) { // only deposit the money when
																						// the user have the same type
																						// and enough amount

			holding.put(currencyType, holding.get(currencyType) - amount);

			walletRepo.save(u.getWallet());
			userRepo.save(u);
			return true;
		} else {
			// if user either does not have that type of currency or does not have enough
			// amount to deposite
			// show error message here ?
			return false;
		}

	}

	@Transactional
	public boolean withdrawMoneyFromWallet(Long userId, String currencyType, Double amount) {
		// error handling for invalid amount
		if (amount < 0)
			return false;
		User u = this.findUserById(userId);

		Map<String, Double> holding = this.getHoldingCurrencyByUserId(userId);

		if (holding.containsKey(currencyType)) {
			if (holding.get(currencyType) < amount) { // user does not have enough money to pay for the trade
				throw new InsufficientFundsException("Insufficient funds in the wallet for currency: " + currencyType);
			} else if (holding.get(currencyType) >= amount) { // check if the wallet have enough money to pay for the
																// trade
				holding.put(currencyType, holding.get(currencyType) - amount);

				walletRepo.save(u.getWallet());
				userRepo.save(u);
				return true;
			}
		}

		return false;

	}

	@Transactional
	public boolean depositMoneyToWallet(Long userId, String currencyType, Double amount) {
		// error handling for invalid amount
		if (amount < 0)
			return false;
		User u = this.findUserById(userId);

		Map<String, Double> holding = this.getHoldingCurrencyByUserId(userId);

		if (holding.containsKey(currencyType)) {
			holding.put(currencyType, holding.get(currencyType) + amount);
			walletRepo.save(u.getWallet());
			userRepo.save(u);

			return true;
		} else {
			holding.put(currencyType, amount);
			walletRepo.save(u.getWallet());
			userRepo.save(u);

			return true;
		}
	}

	public User findUserByUsername(String name) {
		User user = userRepo.findByUsername(name)
				.orElseThrow(() -> new UserNotFoundException("User with name " + name + " Not Found"));

		return user;
	}

	public List<Trade> getAllTradesByUserId(Long id) {
		User u = this.findUserById(id); // error handling for existing user
		return userRepo.findTradesByUserId(u.getUserId());
	}

	public List<Trade> getAllTradesByUsername(String name) {
		User u = this.findUserByUsername(name); // error handling for existing user
		return this.getAllTradesByUserId(u.getUserId());
	}

	public List<Trade> getAllPendingTradeByUserId(Long id) {
		User u = this.findUserById(id);
		return userRepo.findTradesByUserIdAndTradeStatus(u.getUserId(), TRADESTATUS.pending);
	}

	public List<Trade> getAllPendingTradeByUsername(String name) {
		User u = this.findUserByUsername(name);
		return this.getAllPendingTradeByUserId(u.getUserId());
	}

	public List<Trade> getAllMatchedForwardByUserId(Long id) {
		User u = this.findUserById(id);
		return userRepo.findTradesByUserIdAndTradeStatus(u.getUserId(), TRADESTATUS.forwardMatchedPendingSettlement);
	}

	public List<Trade> getAllMatchedForwardByUsername(String name) {
		User u = this.findUserByUsername(name);
		return this.getAllMatchedForwardByUserId(u.getUserId());
	}

}