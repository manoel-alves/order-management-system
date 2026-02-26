package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateProductRequest;
import br.com.manoel.ordermanagement.exception.GlobalExceptionHandler;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.service.ProductService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    // MOCKS

    private Product createMockProduct(Long id, String description, BigDecimal price, int stockQuantity) {
        Product product = new Product(description, price, stockQuantity, Instant.now());
        product.setId(id);
        return product;
    }
    
    // CREATE

    @Test
    @DisplayName("POST /products - Create valid product should return 201")
    void create_validProduct_shouldReturn201() throws Exception {
        Product product = createMockProduct(1L, "Product A", new BigDecimal("100.00"), 10);

        when(productService.create("Product A", new BigDecimal("100.00"), 10)).thenReturn(product);

        CreateProductRequest request = new CreateProductRequest("Product A", new BigDecimal("100.00"), 10);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/products/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Product A"))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    @DisplayName("POST /products - Blank description should return 400")
    void create_blankDescription_shouldReturn400() throws Exception {
        CreateProductRequest request = new CreateProductRequest("", new BigDecimal("100.00"), 10);

        when(productService.create("", new BigDecimal("100.00"), 10))
                .thenThrow(new DomainValidationException("Descrição vazia ou inexistente"));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Descrição vazia ou inexistente"));
    }

    @Test
    @DisplayName("POST /products - Price zero or negative should return 400")
    void create_invalidPrice_shouldReturn400() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Product A", new BigDecimal("0"), 10);

        when(productService.create("Product A", new BigDecimal("0"), 10))
                .thenThrow(new DomainValidationException("O valor deve ser maior que zero"));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O valor deve ser maior que zero"));
    }

    @Test
    @DisplayName("POST /products - Negative stock should return 400")
    void create_negativeStock_shouldReturn400() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Product A", new BigDecimal("100.00"), -5);

        when(productService.create("Product A", new BigDecimal("100.00"), -5))
                .thenThrow(new DomainValidationException("O estoque não pode ser negativo"));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O estoque não pode ser negativo"));
    }

    // GET /products/{id}

    @Test
    @DisplayName("GET /products/{id} - Existing product should return 200")
    void getById_existingProduct_shouldReturn200() throws Exception {
        Product product = createMockProduct(1L, "Product A", new BigDecimal("100.00"), 10);

        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Product A"))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    @DisplayName("GET /products/{id} - Non-existing product should return 404")
    void getById_nonExistingProduct_shouldReturn404() throws Exception {
        when(productService.findById(404L))
                .thenThrow(new ResourceNotFoundException("Produto não encontrado"));

        mockMvc.perform(get("/products/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado"));
    }

    // GET /products?description=<desc>

    @Test
    @DisplayName("GET /products?description=<desc> - Existing products should return 200 and list")
    void getByDescription_existingProducts_shouldReturn200AndList() throws Exception {
        Product product = createMockProduct(1L, "Product A", new BigDecimal("100.00"), 10);

        when(productService.findByDescription("Product A")).thenReturn(List.of(product));

        mockMvc.perform(get("/products").param("description", "Product A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Product A"))
                .andExpect(jsonPath("$[0].price").value(100.00))
                .andExpect(jsonPath("$[0].stockQuantity").value(10));
    }

    @Test
    @DisplayName("GET /products?description=<desc> - No product found should return 200 and empty list")
    void getByDescription_noProductsFound_shouldReturn200AndEmptyList() throws Exception {
        when(productService.findByDescription("Unknown")).thenReturn(List.of());

        mockMvc.perform(get("/products").param("description", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // GET /products

    @Test
    @DisplayName("GET /products - Return all products")
    void getAll_shouldReturn200AndAllProducts() throws Exception {
        Product p1 = createMockProduct(1L, "A", new BigDecimal("10.00"), 5);
        Product p2 = createMockProduct(2L, "B", new BigDecimal("20.00"), 8);

        when(productService.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /products - No products registered should return 200 and empty list")
    void getAll_noProductsRegistered_shouldReturn200AndEmptyList() throws Exception {
        when(productService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}