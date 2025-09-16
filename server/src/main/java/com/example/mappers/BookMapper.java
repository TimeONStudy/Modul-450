package com.example.mappers;

import com.example.data.model.entity.Book;

public class BookMapper {
	public static com.example.rest.generated.model.Book toRest(Book entity) {
		if (entity == null) {
			return null;
		}

		return com.example.rest.generated.model.Book.builder()
				.id(entity.getId())
				.name(entity.getName())
				.author(entity.getAuthor())
				.category(CategoryMapper.toRest(entity.getCategory()))
				.available(entity.getAvailable())
				.build();
	}
}
