package com.example.service;

import com.example.data.model.entity.User;
import java.util.List;

public interface UserService {
	List<User> getAllUsers();
	User getUserById(String id);
	boolean canUserRentBookInCategory(String userId, String categoryId);
}
