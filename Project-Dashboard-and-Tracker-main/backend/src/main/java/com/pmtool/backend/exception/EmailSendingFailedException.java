package com.pmtool.backend.exception;

public class EmailSendingFailedException extends RuntimeException {

	public EmailSendingFailedException(String message) {
		super(message);
	}

	public EmailSendingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
