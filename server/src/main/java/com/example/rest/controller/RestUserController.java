package com.example.rest.controller;

import com.example.data.model.entity.Book;
import com.example.mappers.BookMapper;
import com.example.mappers.UserMapper;
import com.example.rest.generated.UsersApi;
import com.example.rest.generated.model.BookList;
import com.example.rest.generated.model.User;
import com.example.rest.generated.model.UserList;
import com.example.service.BookService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class RestUserController implements UsersApi {
	private final UserService userService;
	private final BookService bookService;

	@Override
    @GetMapping("/getUserById/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable String userId) {
		com.example.data.model.entity.User user = userService.getUserById(userId);
		return ResponseEntity.ok(UserMapper.toRest(user));
	}

	@Override
    @GetMapping("/getUserRentedBooks/{userId}")
	public ResponseEntity<BookList> getUserRentedBooks(@PathVariable String userId) {
		List<Book> rentedBooks = bookService.getUserRentedBooks(userId);
		List<com.example.rest.generated.model.Book> restBooks = rentedBooks.stream()
				.map(BookMapper::toRest)
				.toList();

		BookList bookList = BookList.builder()
				.books(restBooks)
				.build();
		return ResponseEntity.ok(bookList);
	}

	@Override
    @GetMapping("/getUsers")
	public ResponseEntity<UserList> getUsers() {
		List<com.example.data.model.entity.User> users = userService.getAllUsers();
		List<User> restUsers = users.stream()
				.map(UserMapper::toRest)
				.toList();

		UserList userList = UserList.builder()
				.users(restUsers)
				.build();
		return ResponseEntity.ok(userList);
	}
}
