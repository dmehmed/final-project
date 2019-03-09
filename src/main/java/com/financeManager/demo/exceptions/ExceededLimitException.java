package com.financeManager.demo.exceptions;

public class ExceededLimitException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -728603567365030296L;

	public ExceededLimitException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExceededLimitException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ExceededLimitException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ExceededLimitException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ExceededLimitException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
