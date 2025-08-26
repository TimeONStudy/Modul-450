package com.example.m450_lb1.exceptions;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String message) {
        super(message);
    }
}

