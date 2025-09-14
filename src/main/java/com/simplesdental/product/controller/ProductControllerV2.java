package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ProductCreateRequestV2;
import com.simplesdental.product.dto.ProductResponseDTOV2;
import com.simplesdental.product.mapper.ProductMapperV2;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.ProductService;
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
@RequestMapping("/api/v2/products")
public class ProductControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(ProductControllerV2.class);

    private final ProductService productService;
    private final ProductMapperV2 productMapper;

    @Autowired
    public ProductControllerV2(ProductService productService, ProductMapperV2 productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public Page<ProductResponseDTOV2> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("Product V2 - Fetching products - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponseDTOV2> result = productService.findAllWithCategory(pageable)
                .map(productMapper::toDTO);

        logger.info("Product V2 - Found {} products in page {} of {}", result.getNumberOfElements(), result.getNumber(), result.getTotalPages());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTOV2> getProductById(@PathVariable Long id) {
        logger.info("Product V2 - Fetching product by id: {}", id);

        return productService.findByIdWithCategory(id)
                .map(product -> {
                    logger.info("Product V2 - Product found: {}", product.getName());
                    return ResponseEntity.ok(productMapper.toDTO(product));
                })
                .orElseGet(() -> {
                    logger.warn("Product V2 - Product not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTOV2 createProduct(@Valid @RequestBody ProductCreateRequestV2 request) {
        logger.info("Product V2 - Creating new product: {}", request.getName());

        try {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStatus(request.getStatus());
            product.setCode(request.getCode());
            product.setCategory(request.getCategory());

            Product savedProduct = productService.save(product);
            logger.info("Product V2 - Product created successfully with id: {}", savedProduct.getId());
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            logger.error("Product V2 - Error creating product: {}", request.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTOV2> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateRequestV2 request) {
        logger.info("Product V2 - Updating product with id: {}", id);

        return productService.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(request.getName());
                    existingProduct.setDescription(request.getDescription());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setStatus(request.getStatus());
                    existingProduct.setCode(request.getCode());
                    existingProduct.setCategory(request.getCategory());

                    Product updatedProduct = productService.save(existingProduct);
                    logger.info("Product V2 - Product updated successfully: {}", updatedProduct.getName());
                    return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
                })
                .orElseGet(() -> {
                    logger.warn("Product V2 - Product not found for update with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Product V2 - Deleting product with id: {}", id);

        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    logger.info("Product V2 - Product deleted successfully: {}", product.getName());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Product V2 - Product not found for deletion with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}