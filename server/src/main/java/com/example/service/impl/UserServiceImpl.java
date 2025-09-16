package com.example.service.impl;

import com.example.data.model.entity.User;
import com.example.exceptions.ResourceNotFoundException;
import com.example.data.repository.UserRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserById(String id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean canUserRentBookInCategory(String userId, String categoryId) {
		int rentedCount = userRepository.countRentedBooksByCategoryAndUser(userId, categoryId);
		return rentedCount < 3;
	}
}