package com.example.m450_lb1.service;

import com.example.m450_lb1.data.model.dao.User;
import com.example.m450_lb1.data.model.entity.Book;
import com.example.m450_lb1.data.repository.BookRepository;
import com.example.m450_lb1.exceptions.BookNotFoundException;
import com.example.m450_lb1.exceptions.BookNotRentedException;
import com.example.m450_lb1.exceptions.UnauthorizedUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceReturnTest {

	@Mock
	BookRepository bookRepository;

	@InjectMocks
	BookService bookService;

	private User user;
	private Book book;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setAuthenticatedUser(true);
		user.setRentedBooks(new ArrayList<>());

		book = new Book();
		book.setId(1L);
		book.setName("The Hobbit");
		book.setAuthor("J.R.R. Tolkien");
		book.setAvailable(false);
	}

	@Test
	void returnBook_NoOverdue_FeeZero() {
		when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

		user.getRentedBooks().add(book);
		user.getLastRentedBooks().put(book.getId(), LocalDate.now().minusDays(BookService.MAX_BORROW_DAYS));

		BookService.ReturnReceipt receipt = bookService.returnBook("The Hobbit", user);

		assertEquals(book.getId(), receipt.bookId());
		assertEquals("The Hobbit", receipt.bookName());
		assertEquals(0, receipt.overdueDays());
		assertEquals(0.0, receipt.fee());
		assertTrue(book.isAvailable());
		assertFalse(user.getRentedBooks().contains(book));
	}

	@Test
	void returnBook_Overdue_ComputesFee() {
		when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

		user.getRentedBooks().add(book);
		user.getLastRentedBooks().put(book.getId(), LocalDate.now().minusDays(18));

		BookService.ReturnReceipt receipt = bookService.returnBook("The Hobbit", user);

		assertEquals(4, receipt.overdueDays());
		assertEquals(4.0, receipt.fee());
	}

	@Test
	void returnBook_VeryOverdue_CappedFee() {
		when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

		user.getRentedBooks().add(book);
		// Borrowed 100 days ago => 86 overdue, but capped to 30
		user.getLastRentedBooks().put(book.getId(), LocalDate.now().minusDays(100));

		BookService.ReturnReceipt receipt = bookService.returnBook("The Hobbit", user);

		assertEquals(100 - BookService.MAX_BORROW_DAYS, receipt.overdueDays());
		assertEquals(BookService.MAX_LATE_FEE, receipt.fee());
	}

	@Test
	void returnBook_UserNotAuthenticated_Throws() {
		user.setAuthenticatedUser(false);
		assertThrows(UnauthorizedUserException.class,
				() -> bookService.returnBook("The Hobbit", user));
	}

	@Test
	void returnBook_BookNotFound_Throws() {
		when(bookRepository.findBookByName("Unknown")).thenReturn(Optional.empty());
		assertThrows(BookNotFoundException.class,
				() -> bookService.returnBook("Unknown", user));
	}

	@Test
	void returnBook_UserDidNotRent_Throws() {
		when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));
		assertThrows(BookNotRentedException.class,
				() -> bookService.returnBook("The Hobbit", user));
	}
}
