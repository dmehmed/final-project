package com.financeManager.demo.exceptions;

public class WrongUsernameException extends Exception {

	public WrongUsernameException() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private static final long serialVersionUID = -6475611334584667224L;


	public WrongUsernameException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public WrongUsernameException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public WrongUsernameException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public WrongUsernameException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}



}
