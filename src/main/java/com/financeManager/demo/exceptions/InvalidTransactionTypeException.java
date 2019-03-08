package com.financeManager.demo.exceptions;

public class InvalidTransactionTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1353912202859185099L;

	public InvalidTransactionTypeException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionTypeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionTypeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidTransactionTypeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
