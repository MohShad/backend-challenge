package com.simplesdental.product.dto;

import java.math.BigDecimal;

public record ProductResponseDTOV2(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Boolean status,
    Integer code,
    Long categoryId,
    String categoryName
) {}