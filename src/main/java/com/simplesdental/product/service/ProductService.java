package com.simplesdental.product.service;

import com.simplesdental.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();

    List<Product> findAllWithCategory();

    Optional<Product> findById(Long id);

    Optional<Product> findByIdWithCategory(Long id);

    Product save(Product product);

    void deleteById(Long id);
}