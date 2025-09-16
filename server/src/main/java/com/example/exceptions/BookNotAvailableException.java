package com.example.exceptions;

// BookNotAvailableException.java
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
