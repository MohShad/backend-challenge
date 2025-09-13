package com.simplesdental.product.service;

import com.simplesdental.product.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {


    List<Category> findAll();

    List<Category> findAllWithProducts();

    Page<Category> findAllWithProducts(Pageable pageable);

    Optional<Category> findById(Long id);

    Optional<Category> findByIdWithProducts(Long id);

    Category save(Category category);

    void deleteById(Long id);
}