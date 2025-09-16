package com.example.rest.controller;

import com.example.mappers.BookMapper;
import com.example.mappers.CategoryMapper;
import com.example.rest.generated.CategoriesApi;
import com.example.rest.generated.model.Book;
import com.example.rest.generated.model.BookList;
import com.example.rest.generated.model.CategoryList;
import com.example.service.BookService;
import com.example.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class RestCategoryController implements CategoriesApi {
	private final CategoryService categoryService;
	private final BookService bookService;

	@Override
	public ResponseEntity<BookList> getBooksByCategory(String categoryId) {
		List<com.example.data.model.entity.Book> books = bookService.getBooksByCategory(categoryId);
		List<Book> restBooks = books.stream()
				.map(BookMapper::toRest)
				.toList();

		BookList bookList = BookList.builder()
				.books(restBooks)
				.build();
		return ResponseEntity.ok(bookList);
	}

	@Override
	public ResponseEntity<CategoryList> getCategories() {
		List<com.example.data.model.entity.Category> categories = categoryService.getAllCategories();
		List<com.example.rest.generated.model.Category> restCategories = categories.stream()
				.map(CategoryMapper::toRest)
				.toList();

		CategoryList categoryList = CategoryList.builder()
				.categories(restCategories)
				.build();
		return ResponseEntity.ok(categoryList);
	}
}
