package com.fdm.velocitytrade.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a duplicate username is used. This exception is
 * typically used to indicate that a submitted username already exist.
 * 
 * @author junfeng.lee
 * @version 0.01
 * @since 10/01/2024
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DuplicateUsernameException extends RuntimeException {

	public DuplicateUsernameException() {
		super();
	}

	public DuplicateUsernameException(String message) {
		super(message);
	}
}
