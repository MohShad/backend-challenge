package com.simplesdental.product.controller;

import com.simplesdental.product.dto.CategoryResponseDTO;
import com.simplesdental.product.mapper.CategoryMapper;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public Page<CategoryResponseDTO> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("Fetching categories - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CategoryResponseDTO> result = categoryService.findAllWithProducts(pageable)
                .map(categoryMapper::toDTO);

        logger.info("Found {} categories in page {} of {}", result.getNumberOfElements(), result.getNumber(), result.getTotalPages());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        logger.info("Fetching category by id: {}", id);

        return categoryService.findByIdWithProducts(id)
                .map(category -> {
                    logger.info("Category found: {}", category.getName());
                    return ResponseEntity.ok(categoryMapper.toDTO(category));
                })
                .orElseGet(() -> {
                    logger.warn("Category not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@Valid @RequestBody Category category) {
        logger.info("Creating new category: {}", category.getName());

        try {
            Category savedCategory = categoryService.save(category);
            logger.info("Category created successfully with id: {}", savedCategory.getId());
            return savedCategory;
        } catch (Exception e) {
            logger.error("Error creating category: {}", category.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        logger.info("Updating category with id: {}", id);

        return categoryService.findById(id)
                .map(existingCategory -> {
                    category.setId(id);
                    Category updatedCategory = categoryService.save(category);
                    logger.info("Category updated successfully: {}", updatedCategory.getName());
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElseGet(() -> {
                    logger.warn("Category not found for update with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("Deleting category with id: {}", id);

        return categoryService.findById(id)
                .map(category -> {
                    categoryService.deleteById(id);
                    logger.info("Category deleted successfully: {}", category.getName());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Category not found for deletion with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}