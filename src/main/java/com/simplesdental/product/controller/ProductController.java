package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ProductCreateRequest;
import com.simplesdental.product.dto.ProductResponseDTO;
import com.simplesdental.product.mapper.ProductMapper;
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
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public Page<ProductResponseDTO> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("Fetching products - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponseDTO> result = productService.findAllWithCategory(pageable)
                .map(productMapper::toDTO);

        logger.info("Found {} products in page {} of {}", result.getNumberOfElements(), result.getNumber(), result.getTotalPages());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        logger.info("Fetching product by id: {}", id);

        return productService.findByIdWithCategory(id)
                .map(product -> {
                    logger.info("Product found: {}", product.getName());
                    return ResponseEntity.ok(productMapper.toDTO(product));
                })
                .orElseGet(() -> {
                    logger.warn("Product not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductCreateRequest request) {
        logger.info("Creating new product: {}", request.getName());

        try {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStatus(request.getStatus());
            product.setCode(parseCodeFromV1(request.getCode()));
            product.setCategory(request.getCategory());

            Product savedProduct = productService.save(product);
            logger.info("Product created successfully with id: {}", savedProduct.getId());
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            logger.error("Error creating product: {}", request.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateRequest request) {
        logger.info("Updating product with id: {}", id);

        return productService.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(request.getName());
                    existingProduct.setDescription(request.getDescription());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setStatus(request.getStatus());
                    existingProduct.setCode(parseCodeFromV1(request.getCode()));
                    existingProduct.setCategory(request.getCategory());

                    Product updatedProduct = productService.save(existingProduct);
                    logger.info("Product updated successfully: {}", updatedProduct.getName());
                    return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
                })
                .orElseGet(() -> {
                    logger.warn("Product not found for update with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with id: {}", id);

        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    logger.info("Product deleted successfully: {}", product.getName());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Product not found for deletion with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    private Integer parseCodeFromV1(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        if (code.startsWith("PROD-")) {
            try {
                return Integer.parseInt(code.substring(5));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid code format: " + code);
            }
        }

        throw new IllegalArgumentException("Code must start with 'PROD-': " + code);
    }
}