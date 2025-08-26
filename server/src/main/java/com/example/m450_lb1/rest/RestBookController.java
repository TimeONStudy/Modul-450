package com.example.m450_lb1.rest;

import com.example.m450_lb1.data.model.dto.RentRequest;
import com.example.m450_lb1.data.model.dto.ReturnRequest;
import com.example.m450_lb1.data.model.entity.Book;
import com.example.m450_lb1.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class RestBookController {

	private final BookService bookService;

	@PostMapping("/rent")
	public ResponseEntity<Book> rentBook(@RequestBody RentRequest rentRequest) {
		return ResponseEntity.ok(bookService.rentBook(rentRequest.getBookName(), rentRequest.getUser()));
	}

	@PostMapping("/return")
	public ResponseEntity<BookService.ReturnReceipt> returnBook(@RequestBody ReturnRequest returnRequest) {
		return ResponseEntity.ok(bookService.returnBook(returnRequest.getBookName(), returnRequest.getUser()));
	}
}
