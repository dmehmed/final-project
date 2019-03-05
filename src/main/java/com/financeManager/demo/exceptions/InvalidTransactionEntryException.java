package com.financeManager.demo.exceptions;

public class InvalidTransactionEntryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1706728017156984439L;

	public InvalidTransactionEntryException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionEntryException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionEntryException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionEntryException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionEntryException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
