package com.simplesdental.product.service;

import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("19.99"));
        testProduct.setStatus(true);
        testProduct.setCode(123);
        testProduct.setCategory(testCategory);
    }

    @Test
    void shouldSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product savedProduct = productService.save(testProduct);

        assertEquals(testProduct.getId(), savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getCode(), savedProduct.getCode());
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldFindAllProducts() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals(testProduct.getName(), result.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void shouldFindAllProductsWithCategoryAndPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct), pageable, 1);
        when(productRepository.findAllWithCategory(pageable)).thenReturn(productPage);

        Page<Product> result = productService.findAllWithCategory(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(testProduct.getName(), result.getContent().get(0).getName());
        verify(productRepository).findAllWithCategory(pageable);
    }

    @Test
    void shouldFindProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testProduct.getId(), result.get().getId());
        assertEquals(testProduct.getName(), result.get().getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById(999L);

        assertFalse(result.isPresent());
        verify(productRepository).findById(999L);
    }

    @Test
    void shouldFindAllProductsWithCategory() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllWithCategory()).thenReturn(products);

        List<Product> result = productService.findAllWithCategory();

        assertEquals(1, result.size());
        assertEquals(testProduct.getName(), result.get(0).getName());
        verify(productRepository).findAllWithCategory();
    }

    @Test
    void shouldFindProductByIdWithCategory() {
        when(productRepository.findByIdWithCategory(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.findByIdWithCategory(1L);

        assertTrue(result.isPresent());
        assertEquals(testProduct.getId(), result.get().getId());
        assertEquals(testProduct.getName(), result.get().getName());
        verify(productRepository).findByIdWithCategory(1L);
    }

    @Test
    void shouldDeleteProductById() {
        productService.deleteById(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldReturnEmptyWhenProductNotFoundWithCategory() {
        when(productRepository.findByIdWithCategory(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findByIdWithCategory(999L);

        assertFalse(result.isPresent());
        verify(productRepository).findByIdWithCategory(999L);
    }
}