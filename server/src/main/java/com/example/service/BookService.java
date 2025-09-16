package com.example.service;

import com.example.data.model.entity.Book;
import com.example.rest.generated.model.RentBookRequest;
import com.example.rest.generated.model.ReturnBookRequest;
import java.util.List;

public interface BookService {
	List<Book> getAllBooks();
	Book getBookById(String id);
	List<Book> getBooksByCategory(String categoryId);
	List<Book> getUserRentedBooks(String userId);
	Book rentBook(RentBookRequest request);
	Book returnBook(ReturnBookRequest request);
}