package com.example.m450_lb1.service;

import com.example.m450_lb1.data.model.dao.User;
import com.example.m450_lb1.data.model.entity.Book;
import com.example.m450_lb1.data.repository.BookRepository;
import com.example.m450_lb1.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;

	public static final int MAX_BORROW_DAYS = 14;
	public static final double DAILY_LATE_FEE = 1.00;
	public static final double MAX_LATE_FEE = 30.00;

	/**
	 * Rent a book to a user.
	 */
	public Book rentBook(String bookName, User user) {
		if (user == null || !user.isAuthenticatedUser()) {
			throw new UnauthorizedUserException("Only authenticated users can rent books.");
		}

		Book book = bookRepository.findBookByName(bookName)
				.orElseThrow(() -> new BookNotFoundException("Book '%s' not found.".formatted(bookName)));

		if (!book.isAvailable()) {
			throw new BookNotAvailableException("Book '%s' is not available.".formatted(bookName));
		}

		LocalDate lastRented = user.getLastRentedBooks().get(book.getId());
		if (lastRented != null && lastRented.plusMonths(1).isAfter(LocalDate.now())) {
			throw new BookRentedRecentlyException("You can only rent the same book once per month.");
		}

		if (user.getRentedBooks() != null && user.getRentedBooks().contains(book)) {
			throw new BookAlreadyRentedException("User has already rented the book '" + bookName + "'.");
		}

		if (user.getRentedBooks() == null) {
			throw new IllegalStateException("User.rentedBooks list must be initialized.");
		}
		user.getRentedBooks().add(book);
		user.getLastRentedBooks().put(book.getId(), LocalDate.now());

		book.setAvailable(false);

		return book;
	}

	/**
	 * Return a book and compute late fee if the borrow period exceeded the limit.
	 */
	public ReturnReceipt returnBook(String bookName, User user) {
		if (user == null || !user.isAuthenticatedUser()) {
			throw new UnauthorizedUserException("Only authenticated users can return books.");
		}

		Book book = bookRepository.findBookByName(bookName)
				.orElseThrow(() -> new BookNotFoundException("Book '%s' not found.".formatted(bookName)));

		if (user.getRentedBooks() == null || !user.getRentedBooks().contains(book)) {
			throw new BookNotRentedException("User did not rent the book '" + bookName + "'.");
		}

		LocalDate rentedOn = user.getLastRentedBooks().get(book.getId());
		if (rentedOn == null) {
			throw new BookNotRentedException("No rental record found for '" + bookName + "'.");
		}

		long daysBorrowed = ChronoUnit.DAYS.between(rentedOn, LocalDate.now());
		long overdueDays = Math.max(0, daysBorrowed - MAX_BORROW_DAYS);
		double fee = Math.min(overdueDays * DAILY_LATE_FEE, MAX_LATE_FEE);
		
		user.getRentedBooks().remove(book);
		book.setAvailable(true);

		return new ReturnReceipt(book.getId(), book.getName(), overdueDays, fee);
	}

	public record ReturnReceipt(Long bookId, String bookName, long overdueDays, double fee) {}
}
