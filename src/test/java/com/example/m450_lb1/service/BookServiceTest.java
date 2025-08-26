package com.example.m450_lb1.service;

import com.example.m450_lb1.data.model.dao.User;
import com.example.m450_lb1.data.model.entity.Book;
import com.example.m450_lb1.data.repository.BookRepository;
import com.example.m450_lb1.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private User user;
    private Book book;

    @BeforeEach
    void setup() {
        book = new Book();
        book.setId(1L);
        book.setName("The Hobbit");
        book.setAuthor("Tolkien");
        book.setAvailable(true);

        user = new User();
        user.setAuthenticatedUser(true);
        user.setRentedBooks(new ArrayList<>());
    }

    @Test
    void rentBook_ShouldReturnBook_WhenAllConditionsAreMet() {
        when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

        Book result = bookService.rentBook("The Hobbit", user);

        assertEquals(book, result);
        assertTrue(user.getRentedBooks().contains(book));
        verify(bookRepository, times(1)).findBookByName("The Hobbit");
    }

    @Test
    void rentBook_ShouldThrow_WhenBookNotFound() {
        when(bookRepository.findBookByName("Unknown Book")).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.rentBook("Unknown Book", user));

        verify(bookRepository).findBookByName("Unknown Book");
    }

    @Test
    void rentBook_ShouldThrow_WhenBookIsNotAvailable() {
        book.setAvailable(false);
        when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class, () -> bookService.rentBook("The Hobbit", user));
    }


    @Test
    void rentBook_ShouldThrow_WhenUserIsNotAuthenticated() {
        user.setAuthenticatedUser(false);

        assertThrows(UnauthorizedUserException.class, () -> bookService.rentBook("The Hobbit", user));
    }

    @Test
    void rentBook_ShouldThrow_WhenBookAlreadyRentedByUser() {
        user.getRentedBooks().add(book);
        when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

        assertThrows(BookAlreadyRentedException.class, () -> bookService.rentBook("The Hobbit", user));
    }

    @Test
    void rentBook_ShouldThrow_WhenBookWasRentedThisMonth() {
        when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

        Map<Long, LocalDate> lastRentedBooks = new HashMap<>();
        lastRentedBooks.put(book.getId(), LocalDate.now().minusDays(15));
        user.setLastRentedBooks(lastRentedBooks);

        assertThrows(BookRentedRecentlyException.class, () -> bookService.rentBook("The Hobbit", user));
    }

    @Test
    void rentBook_ShouldSucceed_WhenBookWasLastRentedOverAMonthAgo() {
        when(bookRepository.findBookByName("The Hobbit")).thenReturn(Optional.of(book));

        Map<Long, LocalDate> lastRentedBooks = new HashMap<>();
        lastRentedBooks.put(book.getId(), LocalDate.now().minusMonths(2));
        user.setLastRentedBooks(lastRentedBooks);

        Book result = bookService.rentBook("The Hobbit", user);

        assertEquals(book, result);
        assertTrue(user.getRentedBooks().contains(book));
        assertEquals(LocalDate.now(), user.getLastRentedBooks().get(book.getId()));
    }
}
