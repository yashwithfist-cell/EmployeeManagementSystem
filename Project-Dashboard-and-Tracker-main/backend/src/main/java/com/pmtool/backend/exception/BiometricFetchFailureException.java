package com.pmtool.backend.exception;

public class BiometricFetchFailureException extends RuntimeException {

	public BiometricFetchFailureException(String msg) {
		super(msg);
	}
	
	public BiometricFetchFailureException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
