package com.example.m450_lb1.exceptions;

public class BookNotRentedException extends RuntimeException {
	public BookNotRentedException(String message) {
		super(message);
	}
}
