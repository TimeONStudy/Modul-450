package com.example.service.impl;

import com.example.data.model.entity.Category;
import com.example.exceptions.ResourceNotFoundException;
import com.example.data.repository.CategoryRepository;
import com.example.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Category getCategoryById(String id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
	}
}