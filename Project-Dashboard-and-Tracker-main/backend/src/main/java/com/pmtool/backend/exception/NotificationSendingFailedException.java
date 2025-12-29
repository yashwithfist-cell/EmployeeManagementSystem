package com.pmtool.backend.exception;

public class NotificationSendingFailedException extends RuntimeException {

	public NotificationSendingFailedException(String msg) {
		super(msg);
	}

}
