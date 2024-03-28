package com.fdm.velocitytrade.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a duplicate email is used. This exception is typically
 * used to indicate that a submitted email already exist.
 * 
 * @author junfeng.lee
 * @version 0.01
 * @since 10/01/2024
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DuplicateEmailException extends RuntimeException {

	public DuplicateEmailException() {
		super();
	}

	public DuplicateEmailException(String message) {
		super(message);
	}
}
