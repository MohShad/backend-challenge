package com.simplesdental.product.service;

import com.simplesdental.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();

    List<Product> findAllWithCategory();

    Page<Product> findAllWithCategory(Pageable pageable);

    Optional<Product> findById(Long id);

    Optional<Product> findByIdWithCategory(Long id);

    Product save(Product product);

    void deleteById(Long id);
}