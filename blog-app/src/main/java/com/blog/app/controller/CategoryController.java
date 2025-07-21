package com.blog.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.app.dto.ApiResponse;
import com.blog.app.dto.CategoryDTO;
import com.blog.app.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {
	@Autowired
    private CategoryService categoryService;
	
	@PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(new ApiResponse<>("success", "Category created successfully", created));
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updated = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(new ApiResponse<>("success", "Category updated successfully", updated));
    }
	
	 @DeleteMapping("/{id}")
	    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
	        categoryService.deleteCategory(id);
	        return ResponseEntity.ok(new ApiResponse<>("success", "Category deleted successfully", null));
	    }
	 
	 @GetMapping
	    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
	        List<CategoryDTO> categories = categoryService.getAllCategories();
	        return ResponseEntity.ok(new ApiResponse<>("success", "All categories fetched successfully", categories));
	    }
	 
}
