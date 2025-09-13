package com.simplesdental.product.service;

import com.simplesdental.product.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {


    List<Category> findAll();

    List<Category> findAllWithProducts();

    Optional<Category> findById(Long id);

    Optional<Category> findByIdWithProducts(Long id);

    Category save(Category category);

    void deleteById(Long id);
}