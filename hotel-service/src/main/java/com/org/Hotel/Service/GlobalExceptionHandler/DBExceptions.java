package com.org.Hotel.Service.GlobalExceptionHandler;

public class DBExceptions extends RuntimeException {

	// Extra properties/Constructor Overloading you need can be written here>>
	public DBExceptions() {
		super("Resource Not Found on Server x_X");
	}

	public DBExceptions(String message) {
		super(message);
	}
}