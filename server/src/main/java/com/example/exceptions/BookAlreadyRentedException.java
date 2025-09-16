package com.example.exceptions;

public class BookAlreadyRentedException extends RuntimeException {
    public BookAlreadyRentedException(String message) {
        super(message);
    }
}
