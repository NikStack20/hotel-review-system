package com.rating.api.exceptions;

public class DBExceptions extends RuntimeException {
	public DBExceptions() {
		super("Resource Not Found on Server x_X");
	}

	public DBExceptions(String message) {
		super(message);
	}

}
