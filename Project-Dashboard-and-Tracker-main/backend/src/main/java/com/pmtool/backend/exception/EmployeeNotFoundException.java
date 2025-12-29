package com.pmtool.backend.exception;

public class EmployeeNotFoundException extends RuntimeException {

	public EmployeeNotFoundException(String msg) {
		super(msg);
	}

	public EmployeeNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
