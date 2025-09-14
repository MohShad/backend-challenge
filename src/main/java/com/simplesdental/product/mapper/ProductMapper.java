package com.simplesdental.product.mapper;

import com.simplesdental.product.dto.ProductResponseDTO;
import com.simplesdental.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponseDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus(),
                formatCodeForV1(product.getCode()),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }

    private String formatCodeForV1(Integer code) {
        if (code == null) {
            return null;
        }
        return String.format("PROD-%03d", code);
    }
}