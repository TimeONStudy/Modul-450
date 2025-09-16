package com.example.service;

import com.example.data.model.entity.Category;
import java.util.List;

public interface CategoryService {
	List<Category> getAllCategories();
	Category getCategoryById(String id);
}
