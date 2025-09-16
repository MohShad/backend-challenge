package com.simplesdental.product.controller;

import com.simplesdental.product.dto.CategoryResponseDTO;
import com.simplesdental.product.dto.ErrorResponse;
import com.simplesdental.product.dto.SuccessResponseDTO;
import com.simplesdental.product.mapper.CategoryMapper;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.service.CategoryService;
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
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "API para gerenciamento de categorias de produtos")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    @Operation(summary = "Busca todas as categorias",
            description = "Retorna uma lista paginada de todas as categorias com seus produtos associados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de categorias retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public Page<CategoryResponseDTO> getAllCategories(
            @Parameter(description = "Número da página (começando em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (asc ou desc)", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("Fetching categories - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CategoryResponseDTO> result = categoryService.findAllWithProducts(pageable)
                .map(categoryMapper::toDTO);

        logger.info("Found {} categories in page {} of {}", result.getNumberOfElements(), result.getNumber(), result.getTotalPages());
        return result;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca categoria por ID",
            description = "Retorna uma categoria específica com todos os produtos associados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Categoria encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Categoria não encontrada")
    })
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "ID da categoria", required = true, example = "1")
            @PathVariable Long id) {
        logger.info("Fetching category by id: {}", id);

        Optional<Category> categoryOpt = categoryService.findByIdWithProducts(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            logger.info("Category found: {}", category.getName());
            return ResponseEntity.ok(categoryMapper.toDTO(category));
        } else {
            logger.warn("Category not found with id: {}", id);
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Categoria não encontrada", "/api/categories/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova categoria",
            description = "Cria uma nova categoria de produto. Apenas administradores podem criar categorias.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Categoria criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos fornecidos")
    })
    public Category createCategory(
            @Parameter(description = "Dados da categoria a ser criada", required = true)
            @Valid @RequestBody Category category) {
        logger.info("Creating new category: {}", category.getName());

        try {
            Category savedCategory = categoryService.save(category);
            logger.info("Category created successfully with id: {}", savedCategory.getId());
            return savedCategory;
        } catch (Exception e) {
            logger.error("Error creating category: {}", category.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria",
            description = "Atualiza todos os dados de uma categoria existente. Apenas administradores podem atualizar categorias.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Categoria atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404",
                    description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "ID da categoria", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Novos dados da categoria", required = true)
            @Valid @RequestBody Category category) {
        logger.info("Updating category with id: {}", id);

        Optional<Category> categoryOpt = categoryService.findById(id);
        if (categoryOpt.isPresent()) {
            Category existingCategory = categoryOpt.get();
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            Category updatedCategory = categoryService.save(existingCategory);
            logger.info("Category updated successfully: {}", updatedCategory.getName());
            return ResponseEntity.ok(updatedCategory);
        } else {
            logger.warn("Category not found for update with id: {}", id);
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Categoria não encontrada", "/api/categories/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma categoria",
            description = "Remove uma categoria do sistema. Apenas administradores podem deletar categorias.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "404",
                    description = "Categoria não encontrada")
    })
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "ID da categoria", required = true, example = "1")
            @PathVariable Long id) {
        logger.info("Deleting category with id: {}", id);

        Optional<Category> categoryOpt = categoryService.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            categoryService.deleteById(id);
            logger.info("Category deleted successfully: {}", category.getName());
            SuccessResponseDTO response = new SuccessResponseDTO("Categoria deletada com sucesso");
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Category not found for deletion with id: {}", id);
            ErrorResponse error = new ErrorResponse(404, "Not Found", "Categoria não encontrada", "/api/categories/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}