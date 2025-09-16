package com.example.exceptions;

public class BookNotRentedException extends RuntimeException {
	public BookNotRentedException(String message) {
		super(message);
	}
}
