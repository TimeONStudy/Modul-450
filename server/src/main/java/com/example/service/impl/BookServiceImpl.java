package com.example.service.impl;

import com.example.data.model.entity.Book;
import com.example.data.model.entity.User;
import com.example.exceptions.ResourceNotFoundException;
import com.example.exceptions.BadRequestException;
import com.example.data.repository.BookRepository;
import com.example.data.repository.UserRepository;
import com.example.rest.generated.model.RentBookRequest;
import com.example.rest.generated.model.ReturnBookRequest;
import com.example.service.BookService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	@Override
	@Transactional(readOnly = true)
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Book getBookById(String id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Book> getBooksByCategory(String categoryId) {
		return bookRepository.findByCategoryId(categoryId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Book> getUserRentedBooks(String userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		return bookRepository.findByRenterId(userId);
	}

	@Override
	@Transactional
	public Book rentBook(RentBookRequest request) {
		Book book = bookRepository.findById(request.getBookId())
				.orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

		if (!book.getAvailable()) {
			throw new BadRequestException("Book is not available for rent");
		}

		if (!userService.canUserRentBookInCategory(request.getUserId(), book.getCategory().getId())) {
			throw new BadRequestException("User has already rented maximum books (3) in this category");
		}

		book.setAvailable(false);
		book.setRenter(user);
		book.setRentedDate(LocalDateTime.now());

		return bookRepository.save(book);
	}

	@Override
	@Transactional
	public Book returnBook(ReturnBookRequest request) {
		Book book = bookRepository.findById(request.getBookId())
				.orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

		if (book.getAvailable()) {
			throw new BadRequestException("Book was not rented");
		}

		if (book.getRenter() == null || !book.getRenter().getId().equals(request.getUserId())) {
			throw new BadRequestException("Book is rented by a different user");
		}

		book.setAvailable(true);
		book.setRenter(null);
		book.setRentedDate(null);

		return bookRepository.save(book);
	}
}