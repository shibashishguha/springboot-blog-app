package com.blog.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.app.dto.CategoryDTO;
import com.blog.app.entity.Category;
import com.blog.app.repository.CategoryRepository;
import com.blog.app.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

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
        return entityToDto(categoryRepo.save(dtoToEntity(categoryDTO)));
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return entityToDto(categoryRepo.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }
    
    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepo.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
    }
}
