package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateProductRequest;
import br.com.manoel.ordermanagement.exception.GlobalExceptionHandler;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
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

    // HELPERS
    private Product product(Long id, String description, BigDecimal price, int stockQuantity) {
        Product p = new Product(description, price, stockQuantity);
        p.setId(id);

        return p;
    }

    // POST /products
    @Test
    void create_validProduct_shouldReturn201() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Produto", BigDecimal.valueOf(100), 10);

        when(productService.create(request))
                .thenReturn(product(1L, "Produto", BigDecimal.valueOf(100), 10));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/products/1")))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Produto"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    void create_serviceThrowsDomainValidation_shouldReturn400() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Produto", BigDecimal.valueOf(100), 10);

        when(productService.create(request))
                .thenThrow(new DomainValidationException("Preço deve ser maior que zero"));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Preço deve ser maior que zero"));
    }

    // GET /products/{id}
    @Test
    void getById_existingProduct_shouldReturn200() throws Exception {
        when(productService.findById(1L))
                .thenReturn(product(1L, "Produto", BigDecimal.valueOf(100), 10));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Produto"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    void getById_nonExistingProduct_shouldReturn404() throws Exception {
        when(productService.findById(404L))
                .thenThrow(new ResourceNotFoundException("Produto não encontrado"));

        mockMvc.perform(get("/products/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado"));
    }

    // GET /products?description=<desc>
    @Test
    void getByDescription_existingProducts_shouldReturn200AndList() throws Exception {
        when(productService.findByDescription("Pro"))
                .thenReturn(List.of(
                        product(1L, "Produto A", BigDecimal.valueOf(10), 1),
                        product(2L, "Produto B", BigDecimal.valueOf(20), 2)
                ));

        mockMvc.perform(get("/products").param("description", "Pro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Produto A"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Produto B"));
    }

    @Test
    void getByDescription_noProductsFound_shouldReturn200AndEmptyList() throws Exception {
        when(productService.findByDescription("Nada")).thenReturn(List.of());

        mockMvc.perform(get("/products").param("description", "Nada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // GET /products
    @Test
    void getAll_shouldReturn200AndAllProducts() throws Exception {
        when(productService.findAll())
                .thenReturn(List.of(
                        product(1L, "A", BigDecimal.valueOf(1), 1),
                        product(2L, "B", BigDecimal.valueOf(2), 2)
                ));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAll_noProductsRegistered_shouldReturn200AndEmptyList() throws Exception {
        when(productService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}