package com.example.rest.controller;

import com.example.mappers.BookMapper;
import com.example.rest.generated.BooksApi;
import com.example.rest.generated.model.Book;
import com.example.rest.generated.model.BookList;
import com.example.rest.generated.model.RentBookRequest;
import com.example.rest.generated.model.RentBookResponse;
import com.example.rest.generated.model.ReturnBookRequest;
import com.example.rest.generated.model.ReturnBookResponse;
import com.example.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class RestBookController implements BooksApi {
	private final BookService bookService;

	@Override
	public ResponseEntity<Book> getBookById(String bookId) {
		com.example.data.model.entity.Book book = bookService.getBookById(bookId);
		return ResponseEntity.ok(BookMapper.toRest(book));
	}

	@Override
	public ResponseEntity<BookList> getBooks() {
		List<com.example.data.model.entity.Book> books = bookService.getAllBooks();
		List<Book> restBooks = books.stream()
				.map(BookMapper::toRest)
				.toList();

		BookList bookList = BookList.builder()
				.books(restBooks)
				.build();
		return ResponseEntity.ok(bookList);
	}

	@Override
	public ResponseEntity<RentBookResponse> rentBook(RentBookRequest rentBookRequest) {
		try {
			com.example.data.model.entity.Book rentedBook = bookService.rentBook(rentBookRequest);

			RentBookResponse response = RentBookResponse.builder()
					.success(true)
					.message("Book successfully rented")
					.book(BookMapper.toRest(rentedBook))
					.rentedDate(rentedBook.getRentedDate().atOffset(ZoneOffset.UTC))
					.build();

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			RentBookResponse response = RentBookResponse.builder()
					.success(false)
					.message(e.getMessage())
					.build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@Override
	public ResponseEntity<ReturnBookResponse> returnBook(ReturnBookRequest returnBookRequest) {
		try {
			com.example.data.model.entity.Book returnedBook = bookService.returnBook(returnBookRequest);

			ReturnBookResponse response = ReturnBookResponse.builder()
					.success(true)
					.message("Book successfully returned")
					.book(BookMapper.toRest(returnedBook))
					.returnedDate(java.time.OffsetDateTime.now())
					.build();

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ReturnBookResponse response = ReturnBookResponse.builder()
					.success(false)
					.message(e.getMessage())
					.build();
			return ResponseEntity.badRequest().body(response);
		}
	}
}
