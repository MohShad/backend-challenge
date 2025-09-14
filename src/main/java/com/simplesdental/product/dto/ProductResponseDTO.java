package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response de produto V1 com código no formato PROD-XXX")
public class ProductResponseDTO {

    @Schema(description = "ID único do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Smartphone Samsung Galaxy S24")
    private String name;

    @Schema(description = "Descrição detalhada do produto", example = "Smartphone com tela de 6.1 polegadas, câmera de 64MP e 128GB de armazenamento")
    private String description;

    @Schema(description = "Preço do produto em reais", example = "1299.99")
    private BigDecimal price;

    @Schema(description = "Status ativo/inativo do produto", example = "true")
    private Boolean status;

    @Schema(description = "Código do produto no formato PROD-XXX", example = "PROD-001")
    private String code;

    @Schema(description = "ID da categoria", example = "1")
    private Long categoryId;

    @Schema(description = "Nome da categoria", example = "Eletrônicos")
    private String categoryName;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Long id, String name, String description, BigDecimal price,
                              Boolean status, String code, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.code = code;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}