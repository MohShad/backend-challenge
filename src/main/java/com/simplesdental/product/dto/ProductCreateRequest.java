package com.simplesdental.product.dto;

import com.simplesdental.product.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request para criar produto V1 com código no formato PROD-XXX")
public class ProductCreateRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome do produto", example = "Smartphone Samsung Galaxy S24")
    private String name;

    @Size(max = 255)
    @Schema(description = "Descrição detalhada do produto", example = "Smartphone com tela de 6.1 polegadas, câmera de 64MP e 128GB de armazenamento")
    private String description;

    @NotNull
    @Positive
    @Schema(description = "Preço do produto em reais", example = "1299.99")
    private BigDecimal price;

    @NotNull
    @Schema(description = "Status ativo/inativo do produto", example = "true")
    private Boolean status;

    @Schema(description = "Código do produto no formato PROD-XXX", example = "PROD-001")
    private String code;

    @NotNull
    @Schema(description = "Categoria do produto", implementation = Category.class)
    private Category category;

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}