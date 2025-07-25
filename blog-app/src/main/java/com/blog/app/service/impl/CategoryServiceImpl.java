package com.blog.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blog.app.dto.CategoryDTO;
import com.blog.app.entity.Category;
import com.blog.app.repository.CategoryRepository;
import com.blog.app.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepo;

    private Category dtoToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    private CategoryDTO entityToDto(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
    	 logger.info("Creating category with name: {}", categoryDTO.getName());
    	 Category saved = categoryRepo.save(dtoToEntity(categoryDTO));
         logger.debug("Category created with ID: {}", saved.getId());
         return entityToDto(saved);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
    	logger.info("Updating category with ID: {}", id);
        Category category = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        Category updated = categoryRepo.save(category);
        logger.debug("Category updated successfully with ID: {}", updated.getId());
        return entityToDto(updated);
    }

    @Override
    public void deleteCategory(Long id) {
    	logger.info("Deleting category with ID: {}", id);
        categoryRepo.deleteById(id);
        logger.debug("Category deleted successfully with ID: {}", id);
    }
    
    @Override
    public List<CategoryDTO> getAllCategories() {
    	logger.info("Fetching all categories");
        List<CategoryDTO> categoryList = categoryRepo.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
        logger.debug("Total categories fetched: {}", categoryList.size());
        return categoryList;
    }
}
