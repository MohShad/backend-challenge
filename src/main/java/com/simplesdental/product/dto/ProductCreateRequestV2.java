package com.simplesdental.product.dto;

import com.simplesdental.product.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Request para criar produto V2 com código inteiro")
public class ProductCreateRequestV2 {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome do produto", example = "iPhone 15 Pro")
    private String name;

    @Size(max = 255)
    @Schema(description = "Descrição detalhada do produto", example = "iPhone 15 Pro com chip A17 Pro, câmera de 48MP e 256GB de armazenamento")
    private String description;

    @NotNull
    @Positive
    @Schema(description = "Preço do produto em reais", example = "7999.00")
    private BigDecimal price;

    @NotNull
    @Schema(description = "Status ativo/inativo do produto", example = "true")
    private Boolean status;

    @NotNull
    @Min(value = 1)
    @Schema(description = "Código do produto (inteiro)", example = "150")
    private Integer code;

    @NotNull
    @Schema(description = "Categoria do produto", implementation = Category.class)
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