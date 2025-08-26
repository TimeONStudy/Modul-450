package com.example.m450_lb1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<String> handleBookNotFound(BookNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(BookNotAvailableException.class)
	public ResponseEntity<String> handleBookUnavailable(BookNotAvailableException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedUserException.class)
	public ResponseEntity<String> handleUnauthorized(UnauthorizedUserException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	@ExceptionHandler(BookAlreadyRentedException.class)
	public ResponseEntity<String> handleAlreadyRented(BookAlreadyRentedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(BookNotRentedException.class)
	public ResponseEntity<String> handleNotRented(BookNotRentedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}
