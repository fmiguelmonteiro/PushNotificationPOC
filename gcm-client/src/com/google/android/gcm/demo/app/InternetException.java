package com.google.android.gcm.demo.app;

public class InternetException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4542676673926741196L;
	
	public InternetException(String message){
		super(message);		
	}
	
	public InternetException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
