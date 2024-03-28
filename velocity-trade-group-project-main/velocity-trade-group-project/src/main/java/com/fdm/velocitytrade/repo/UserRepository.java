package com.fdm.velocitytrade.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fdm.velocitytrade.TRADESTATUS;
import com.fdm.velocitytrade.model.Trade;
import com.fdm.velocitytrade.model.User;

/**
 * Repository interface for managing User entities. Extends JpaRepository to
 * inherit basic CRUD operations.
 * 
 * @author junfeng.lee
 * @version 0.02
 * @since 04/01/2024
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Finds a user by their username.
	 *
	 * @param username The username of the user to find.
	 * @return The user with the specified username, or null if not found.
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Finds users by their email address.
	 *
	 * @param email The email address to search for.
	 * @return A list of users with the specified email address.
	 */
	List<User> findByEmail(String email);

	boolean existsByUsername(String username);

	/**
	 * Finds users by their email address.
	 *
	 * @param email The email address to search for.
	 * @return true If email already
	 */
	boolean existsByEmail(String email);

	String findUsernameByUserId(long userId);
	@Query("SELECT t FROM Trade t JOIN t.user u WHERE u.id = :userId")
    List<Trade> findTradesByUserId(long userId);	
	
	@Query("SELECT t FROM Trade t WHERE t.user.userId = :userId AND t.tradeStatus = :tradeStatus")
    List<Trade> findTradesByUserIdAndTradeStatus(Long userId, TRADESTATUS tradeStatus);
}
