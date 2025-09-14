package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ErrorResponse;
import com.simplesdental.product.dto.ProductCreateRequest;
import com.simplesdental.product.dto.ProductResponseDTO;
import com.simplesdental.product.mapper.ProductMapper;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products API V1", description = "API V1 para gerenciamento de produtos com códigos no formato PROD-XXX")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os produtos V1",
            description = "Retorna uma lista paginada de produtos com códigos no formato PROD-XXX"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de produtos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public Page<ProductResponseDTO> getAllProducts(
            @Parameter(description = "Número da página (começando em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("Fetching products - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponseDTO> result = productService.findAllWithCategory(pageable)
                .map(productMapper::toDTO);

        logger.info("Found {} products in page {} of {}", result.getNumberOfElements(), result.getNumber(), result.getTotalPages());
        return result;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar produto por ID V1",
            description = "Retorna um produto específico por ID com código no formato PROD-XXX"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        logger.info("Fetching product by id: {}", id);

        return productService.findByIdWithCategory(id)
                .map(product -> {
                    logger.info("Product found: {}", product.getName());
                    return ResponseEntity.ok(productMapper.toDTO(product));
                })
                .orElseGet(() -> {
                    logger.warn("Product not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar novo produto V1",
            description = "Cria um novo produto com código no formato PROD-XXX"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductCreateRequest request) {
        logger.info("Creating new product: {}", request.getName());

        try {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStatus(request.getStatus());
            product.setCode(parseCodeFromV1(request.getCode()));
            product.setCategory(request.getCategory());

            Product savedProduct = productService.save(product);
            logger.info("Product created successfully with id: {}", savedProduct.getId());
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            logger.error("Error creating product: {}", request.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar produto V1",
            description = "Atualiza um produto existente com código no formato PROD-XXX"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody ProductCreateRequest request) {
        logger.info("Updating product with id: {}", id);

        return productService.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(request.getName());
                    existingProduct.setDescription(request.getDescription());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setStatus(request.getStatus());
                    existingProduct.setCode(parseCodeFromV1(request.getCode()));
                    existingProduct.setCategory(request.getCategory());

                    Product updatedProduct = productService.save(existingProduct);
                    logger.info("Product updated successfully: {}", updatedProduct.getName());
                    return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
                })
                .orElseGet(() -> {
                    logger.warn("Product not found for update with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir produto V1",
            description = "Remove um produto do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Produto excluído com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        logger.info("Deleting product with id: {}", id);

        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    logger.info("Product deleted successfully: {}", product.getName());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Product not found for deletion with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    private Integer parseCodeFromV1(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        if (code.startsWith("PROD-")) {
            try {
                return Integer.parseInt(code.substring(5));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid code format: " + code);
            }
        }

        throw new IllegalArgumentException("Code must start with 'PROD-': " + code);
    }
}