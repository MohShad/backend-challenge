package com.simplesdental.product.service;

import com.simplesdental.product.model.Category;
import com.simplesdental.product.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
    }

    @Test
    void shouldFindAllCategories() {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");
        List<Category> categories = Arrays.asList(testCategory, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAll();

        assertEquals(2, result.size());
        assertEquals(testCategory.getName(), result.get(0).getName());
        assertEquals(category2.getName(), result.get(1).getName());
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldFindAllCategoriesWithProducts() {
        List<Category> categories = Arrays.asList(testCategory);

        when(categoryRepository.findAllWithProducts()).thenReturn(categories);

        List<Category> result = categoryService.findAllWithProducts();

        assertEquals(1, result.size());
        assertEquals(testCategory.getName(), result.get(0).getName());
        verify(categoryRepository).findAllWithProducts();
    }

    @Test
    void shouldFindAllCategoriesWithProductsPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(Arrays.asList(testCategory), pageable, 1);

        when(categoryRepository.findAllWithProducts(pageable)).thenReturn(categoryPage);

        Page<Category> result = categoryService.findAllWithProducts(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(testCategory.getName(), result.getContent().get(0).getName());
        verify(categoryRepository).findAllWithProducts(pageable);
    }

    @Test
    void shouldFindCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testCategory.getId(), result.get().getId());
        assertEquals(testCategory.getName(), result.get().getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findById(999L);

        assertFalse(result.isPresent());
        verify(categoryRepository).findById(999L);
    }

    @Test
    void shouldFindCategoryByIdWithProducts() {
        when(categoryRepository.findByIdWithProducts(1L)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.findByIdWithProducts(1L);

        assertTrue(result.isPresent());
        assertEquals(testCategory.getId(), result.get().getId());
        assertEquals(testCategory.getName(), result.get().getName());
        verify(categoryRepository).findByIdWithProducts(1L);
    }

    @Test
    void shouldSaveCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.save(testCategory);

        assertEquals(testCategory.getId(), result.getId());
        assertEquals(testCategory.getName(), result.getName());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void shouldDeleteCategoryById() {
        categoryService.deleteById(1L);

        verify(categoryRepository).deleteById(1L);
    }
}