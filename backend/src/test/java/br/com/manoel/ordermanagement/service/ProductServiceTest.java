package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.dto.request.CreateProductRequest;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static br.com.manoel.ordermanagement.model.MoneyConstants.MONEY_ROUNDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    private Product product(String description, BigDecimal price, int stock) {
        return new Product(description, price, stock);
    }

    // CREATE PRODUCT
    @Test
    @DisplayName("Create product with valid data should succeed")
    void createProduct_withValidData_shouldSucceed() {
        CreateProductRequest request = new CreateProductRequest("Produto A", BigDecimal.valueOf(100), 10);

        Product saved = product("Produto A", BigDecimal.valueOf(100), 10);
        saved.setId(1L);

        when(repository.save(any(Product.class))).thenReturn(saved);

        Product result = service.create(request);

        assertEquals(1L, result.getId());
        assertEquals("Produto A", result.getDescription());
        assertEquals(BigDecimal.valueOf(100.0).setScale(2, MONEY_ROUNDING), result.getPrice());
        assertEquals(10, result.getStockQuantity());

        verify(repository, times(1)).save(any(Product.class));
    }

    // FIND BY ID
    @Test
    @DisplayName("Find product by null ID should throw")
    void findById_null_shouldThrow() {
        assertThrows(DomainValidationException.class, () -> service.findById(null));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Find product by existing ID should succeed")
    void findById_existingId_shouldReturnProduct() {
        Product p = product("Produto A", BigDecimal.valueOf(100), 10);
        p.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(p));

        Product result = service.findById(1L);
        assertEquals(p, result);
    }

    @Test
    @DisplayName("Find product by non-existing ID should throw")
    void findById_nonExistingId_shouldThrow() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    // FIND BY DESCRIPTION
    // =========================

    @Test
    @DisplayName("Find products by null description should throw")
    void findByDescription_null_shouldThrow() {
        assertThrows(DomainValidationException.class, () -> service.findByDescription(null));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Find products by description should return matching list")
    void findByDescription_shouldReturnMatchingList() {
        Product p = product("Produto A", BigDecimal.valueOf(100), 10);
        when(repository.findByDescription("Produto A")).thenReturn(List.of(p));

        List<Product> result = service.findByDescription("Produto A");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(p, result.getFirst());
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
        Product p1 = product("Produto A", BigDecimal.valueOf(100), 10);
        Product p2 = product("Produto A", BigDecimal.valueOf(120), 5);
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
        Product p1 = product("Produto A", BigDecimal.valueOf(100), 10);
        Product p2 = product("Produto B", BigDecimal.valueOf(50), 5);
        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(p1, result.get(0));
        assertEquals(p2, result.get(1));
    }

    // =========================
    // DECREMENT STOCK
    // =========================

    @Test
    void decrementStock_nullProductId_shouldThrow() {
        assertThrows(DomainValidationException.class, () -> service.decrementStock(null, 1));
        verifyNoInteractions(repository);
    }

    @Test
    void decrementStock_invalidAmount_shouldThrow() {
        assertThrows(DomainValidationException.class, () -> service.decrementStock(1L, 0));
        verifyNoInteractions(repository);
    }

    @Test
    void decrementStock_insufficientStock_shouldThrow() {
        when(repository.decrementStock(1L, 5)).thenReturn(false);

        assertThrows(DomainValidationException.class, () -> service.decrementStock(1L, 5));
    }

    @Test
    void decrementStock_success_shouldNotThrow() {
        when(repository.decrementStock(1L, 5)).thenReturn(true);

        assertDoesNotThrow(() -> service.decrementStock(1L, 5));
    }
}