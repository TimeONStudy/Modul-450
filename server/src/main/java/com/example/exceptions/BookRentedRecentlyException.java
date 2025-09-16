package com.example.exceptions;

public class BookRentedRecentlyException extends RuntimeException {
    public BookRentedRecentlyException(String message) {
        super(message);
    }
}
