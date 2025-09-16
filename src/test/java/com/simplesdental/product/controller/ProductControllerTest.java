package com.simplesdental.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesdental.product.dto.ProductCreateRequest;
import com.simplesdental.product.dto.ProductResponseDTO;
import com.simplesdental.product.mapper.ProductMapper;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private com.simplesdental.product.util.JwtUtil jwtUtil;

    @MockBean
    private com.simplesdental.product.service.UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private Category testCategory;
    private ProductCreateRequest createRequest;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("19.99"));
        testProduct.setStatus(true);
        testProduct.setCode(123);
        testProduct.setCategory(testCategory);

        createRequest = new ProductCreateRequest();
        createRequest.setName("Test Product");
        createRequest.setDescription("Test Description");
        createRequest.setPrice(new BigDecimal("19.99"));
        createRequest.setStatus(true);
        createRequest.setCode("PROD-123");
        createRequest.setCategory(testCategory);

        responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Test Product");
        responseDTO.setDescription("Test Description");
        responseDTO.setPrice(new BigDecimal("19.99"));
        responseDTO.setStatus(true);
        responseDTO.setCode("PROD-123");
        responseDTO.setCategoryId(1L);
        responseDTO.setCategoryName("Test Category");
    }

    @Test
    @WithMockUser
    void shouldGetAllProductsWithPagination() throws Exception {
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct), PageRequest.of(0, 20), 1);
        when(productService.findAllWithCategory(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDTO(testProduct)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.content[0].name").value(responseDTO.getName()))
                .andExpect(jsonPath("$.content[0].code").value(responseDTO.getCode()))
                .andExpect(jsonPath("$.content[0].categoryId").value(responseDTO.getCategoryId()))
                .andExpect(jsonPath("$.content[0].categoryName").value(responseDTO.getCategoryName()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @WithMockUser
    void shouldGetAllProductsWithCustomPagination() throws Exception {
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct), PageRequest.of(1, 10), 25);
        when(productService.findAllWithCategory(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toDTO(testProduct)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(responseDTO.getName()))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser
    void shouldGetProductById() throws Exception {
        when(productService.findByIdWithCategory(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.name").value(responseDTO.getName()))
                .andExpect(jsonPath("$.code").value(responseDTO.getCode()))
                .andExpect(jsonPath("$.categoryId").value(responseDTO.getCategoryId()))
                .andExpect(jsonPath("$.categoryName").value(responseDTO.getCategoryName()));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenGetProductByIdNotFound() throws Exception {
        when(productService.findByIdWithCategory(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateProductWithValidInput() throws Exception {
        when(productService.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(testProduct)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.name").value(responseDTO.getName()))
                .andExpect(jsonPath("$.code").value(responseDTO.getCode()))
                .andExpect(jsonPath("$.categoryId").value(responseDTO.getCategoryId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productService.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(testProduct)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.name").value(responseDTO.getName()))
                .andExpect(jsonPath("$.code").value(responseDTO.getCode()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUpdateProductNotFound() throws Exception {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productService).deleteById(1L);

        mockMvc.perform(delete("/api/products/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Produto deletado com sucesso"))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenDeleteProductNotFound() throws Exception {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}