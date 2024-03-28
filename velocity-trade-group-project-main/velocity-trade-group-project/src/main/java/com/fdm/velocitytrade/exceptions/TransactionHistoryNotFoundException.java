package com.fdm.velocitytrade.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

	@ResponseStatus(HttpStatus.NOT_FOUND)
	public class TransactionHistoryNotFoundException extends RuntimeException {

		public TransactionHistoryNotFoundException() {
			super();
		}

		public TransactionHistoryNotFoundException(String message) {
			super(message);
		}
}

