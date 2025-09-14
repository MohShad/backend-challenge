package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Response de produto V2 com código inteiro")
public record ProductResponseDTOV2(
        @Schema(description = "ID único do produto", example = "1")
        Long id,

        @Schema(description = "Nome do produto", example = "iPhone 15 Pro")
        String name,

        @Schema(description = "Descrição detalhada do produto", example = "iPhone 15 Pro com chip A17 Pro, câmera de 48MP e 256GB de armazenamento")
        String description,

        @Schema(description = "Preço do produto em reais", example = "7999.00")
        BigDecimal price,

        @Schema(description = "Status ativo/inativo do produto", example = "true")
        Boolean status,

        @Schema(description = "Código do produto (inteiro)", example = "150")
        Integer code,

        @Schema(description = "ID da categoria", example = "1")
        Long categoryId,

        @Schema(description = "Nome da categoria", example = "Eletrônicos")
        String categoryName
) {}