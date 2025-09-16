package com.example.mappers;

import com.example.data.model.entity.User;


public class UserMapper {
	public static com.example.rest.generated.model.User toRest(User entity) {
		if (entity == null) {
			return null;
		}

		return com.example.rest.generated.model.User.builder()
				.id(entity.getId())
				.name(entity.getName())
				.email(entity.getEmail())
				.authenticated(entity.getAuthenticated())
				.build();
	}
}

