package com.simplesdental.product.mapper;

import com.simplesdental.product.dto.ProductResponseDTOV2;
import com.simplesdental.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperV2 {

    public ProductResponseDTOV2 toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponseDTOV2(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStatus(),
            product.getCode(),
            product.getCategory() != null ? product.getCategory().getId() : null,
            product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}