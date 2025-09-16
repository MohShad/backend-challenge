package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ErrorResponse;
import com.simplesdental.product.dto.ProductCreateRequestV2;
import com.simplesdental.product.dto.ProductResponseDTOV2;
import com.simplesdental.product.dto.SuccessResponseDTO;
import com.simplesdental.product.mapper.ProductMapperV2;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
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
@RequestMapping("/api/v2/products")
@Tag(name = "Products API V2", description = "API V2 para gerenciamento de produtos com códigos inteiros")
public class ProductControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(ProductControllerV2.class);

    private final ProductService productService;
    private final ProductMapperV2 productMapper;

    @Autowired
    public ProductControllerV2(ProductService productService, ProductMapperV2 productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os produtos V2",
            description = "Retorna uma lista paginada de produtos com códigos inteiros"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de produtos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public Page<ProductResponseDTOV2> getAllProducts(
            @Parameter(description = "Número da página (começando em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("Product V2 - Fetching products - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponseDTOV2> result = productService.findAllWithCategory(pageable)
                .map(productMapper::toDTO);

        logger.info("Product V2 - Found {} products in page {} of {}", result.getNumberOfElements(), result.getNumber(), result.getTotalPages());
        return result;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar produto por ID V2",
            description = "Retorna um produto específico por ID com código inteiro"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTOV2.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<?> getProductById(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        logger.info("Product V2 - Fetching product by id: {}", id);

        Optional<Product> productOpt = productService.findByIdWithCategory(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            logger.info("Product V2 - Product found: {}", product.getName());
            return ResponseEntity.ok(productMapper.toDTO(product));
        } else {
            logger.warn("Product V2 - Product not found with id: {}", id);
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Produto não encontrado", "/api/v2/products/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar novo produto V2",
            description = "Cria um novo produto com código inteiro. Apenas administradores podem criar produtos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTOV2.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ProductResponseDTOV2 createProduct(@Valid @RequestBody ProductCreateRequestV2 request) {
        logger.info("Product V2 - Creating new product: {}", request.getName());

        try {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStatus(request.getStatus());
            product.setCode(request.getCode());
            product.setCategory(request.getCategory());

            Product savedProduct = productService.save(product);
            logger.info("Product V2 - Product created successfully with id: {}", savedProduct.getId());
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            logger.error("Product V2 - Error creating product: {}", request.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar produto V2",
            description = "Atualiza um produto existente com código inteiro. Apenas administradores podem atualizar produtos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTOV2.class))
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
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody ProductCreateRequestV2 request) {
        logger.info("Product V2 - Updating product with id: {}", id);

        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isPresent()) {
            Product existingProduct = productOpt.get();
            existingProduct.setName(request.getName());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setPrice(request.getPrice());
            existingProduct.setStatus(request.getStatus());
            existingProduct.setCode(request.getCode());
            existingProduct.setCategory(request.getCategory());

            Product updatedProduct = productService.save(existingProduct);
            logger.info("Product V2 - Product updated successfully: {}", updatedProduct.getName());
            return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
        } else {
            logger.warn("Product V2 - Product not found for update with id: {}", id);
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Produto não encontrado", "/api/v2/products/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir produto V2",
            description = "Remove um produto do sistema. Apenas administradores podem excluir produtos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto excluído com sucesso",
                    content = @Content(schema = @Schema(implementation = SuccessResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        logger.info("Product V2 - Deleting product with id: {}", id);

        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            productService.deleteById(id);
            logger.info("Product V2 - Product deleted successfully: {}", product.getName());
            SuccessResponseDTO response = new SuccessResponseDTO("Produto excluído com sucesso");
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Product V2 - Product not found for deletion with id: {}", id);
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Produto não encontrado", "/api/v2/products/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}