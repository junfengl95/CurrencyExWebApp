package com.fdm.velocitytrade.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user is not found. This exception is typically used
 * to indicate that a requested user does not exist.
 * 
 * @author junfeng.lee
 * @version 0.01
 * @since 10/01/2024
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}

}
