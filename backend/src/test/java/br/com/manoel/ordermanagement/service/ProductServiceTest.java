package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    // MOCKS
    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    private Product createProduct(String description, BigDecimal price, int stock) {
        return new Product(description, price, stock);
    }

    // CREATE PRODUCT

    @Test
    @DisplayName("Create product with valid data should succeed")
    void createProduct_withValidData_shouldSucceed() {
        Product saved = createProduct("Produto A", BigDecimal.valueOf(100.0), 10);
        saved.setId(1L);

        when(repository.save(any(Product.class))).thenReturn(saved);

        Product result = service.create("Produto A", BigDecimal.valueOf(100.0), 10);

        assertEquals(1L, result.getId());
        assertEquals("Produto A", result.getDescription());
        assertEquals(BigDecimal.valueOf(100.0).setScale(2, RoundingMode.HALF_UP), result.getPrice());
        assertEquals(10, result.getStockQuantity());

        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Create product with invalid description should throw")
    void createProduct_withInvalidDescription_shouldThrow() {
        assertThrows(DomainValidationException.class, () ->
                service.create("", BigDecimal.valueOf(50.0), 5)
        );
    }

    @Test
    @DisplayName("Create product with invalid price should throw")
    void createProduct_withInvalidPrice_shouldThrow() {
        assertThrows(DomainValidationException.class, () ->
                service.create("Produto B", BigDecimal.valueOf(-10.0), 5)
        );
    }

    @Test
    @DisplayName("Create product with negative stock should throw")
    void createProduct_withNegativeStock_shouldThrow() {
        assertThrows(DomainValidationException.class, () ->
                service.create("Produto C", BigDecimal.valueOf(10.0), -5)
        );
    }

    // FIND BY ID

    @Test
    @DisplayName("Find product by existing ID should succeed")
    void findById_existingId_shouldReturnProduct() {
        Product product = createProduct("Produto A", BigDecimal.valueOf(100.0), 10);
        product.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.findById(1L);
        assertEquals(product, result);
    }

    @Test
    @DisplayName("Find product by non-existing ID should throw")
    void findById_nonExistingId_shouldThrow() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    // FIND BY DESCRIPTION

    @Test
    @DisplayName("Find products by description should return matching list")
    void findByDescription_shouldReturnMatchingList() {
        Product product = createProduct("Produto A", BigDecimal.valueOf(100.0), 10);
        when(repository.findByDescription("Produto A")).thenReturn(List.of(product));

        List<Product> result = service.findByDescription("Produto A");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(product, result.getFirst());
    }

    @Test
    @DisplayName("Find products by description with no match should return empty list")
    void findByDescription_noMatch_shouldReturnEmptyList() {
        when(repository.findByDescription("Produto X")).thenReturn(List.of());

        List<Product> result = service.findByDescription("Produto X");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Find products by description with multiple matches should return all")
    void findByDescription_multipleMatches_shouldReturnAll() {
        Product p1 = createProduct("Produto A", BigDecimal.valueOf(100.0), 10);
        Product p2 = createProduct("Produto A", BigDecimal.valueOf(120.0), 5);
        when(repository.findByDescription("Produto A")).thenReturn(List.of(p1, p2));

        List<Product> result = service.findByDescription("Produto A");

        assertEquals(2, result.size());
        assertEquals(p1, result.get(0));
        assertEquals(p2, result.get(1));
    }

    // FIND ALL PRODUCTS

    @Test
    @DisplayName("Find all products should return all products")
    void findAll_shouldReturnAllProducts() {
        Product p1 = createProduct("Produto A", BigDecimal.valueOf(100.0), 10);
        Product p2 = createProduct("Produto B", BigDecimal.valueOf(50.0), 5);
        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(p1, result.get(0));
        assertEquals(p2, result.get(1));
    }
}