package com.example.mappers;

import com.example.data.model.entity.Category;

public class CategoryMapper {
	public static com.example.rest.generated.model.Category toRest(Category entity) {
		if (entity == null) {
			return null;
		}

		return com.example.rest.generated.model.Category.builder()
				.id(entity.getId())
				.name(entity.getName())
				.build();
	}
}
