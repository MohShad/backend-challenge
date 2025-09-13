package com.simplesdental.product.mapper;

import com.simplesdental.product.dto.CategoryResponseDTO;
import com.simplesdental.product.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        Integer productCount = category.getProducts() != null ? category.getProducts().size() : 0;

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                productCount
        );
    }
}