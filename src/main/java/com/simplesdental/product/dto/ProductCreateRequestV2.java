package com.simplesdental.product.dto;

import com.simplesdental.product.model.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductCreateRequestV2 {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private Boolean status;

    @NotNull
    @Min(value = 1)
    private Integer code;

    @NotNull
    private Category category;

    public ProductCreateRequestV2() {}

    public ProductCreateRequestV2(String name, String description, BigDecimal price, Boolean status, Integer code, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.code = code;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}