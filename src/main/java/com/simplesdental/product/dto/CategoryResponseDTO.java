package com.simplesdental.product.dto;

public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Integer productCount;

    public CategoryResponseDTO() {
    }

    public CategoryResponseDTO(Long id, String name, String description, Integer productCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productCount = productCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
}
