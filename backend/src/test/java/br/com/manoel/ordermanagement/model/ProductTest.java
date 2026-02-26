package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    // CREATE Product

    @Test
    @DisplayName("Create Product with valid data should succeed")
    void createProduct_withValidData_shouldSucceed() {
        Product product = new Product("Valid Product - Description", BigDecimal.valueOf(99.99), 10);

        assertEquals("Valid Product - Description", product.getDescription());
        assertEquals(BigDecimal.valueOf(99.99), product.getPrice());
        assertEquals(10, product.getStockQuantity());
        assertNotNull(product.getCreatedAt());
    }

    @Test
    @DisplayName("Create Product with custom createdAt should respect value")
    void createProduct_withCustomCreatedAt_shouldRespectValue() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Product product = new Product("Valid Description", BigDecimal.valueOf(99.99), 10, now);

        assertEquals(now, product.getCreatedAt());
    }

    // DESCRIPTION

    @Test
    @DisplayName("Create Product with empty description should throw")
    void createProduct_withEmptyDescription_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("", BigDecimal.valueOf(99.99), 10)
        );
        assertEquals("Descrição vazia ou inexistente", ex.getMessage());
    }

    @Test
    @DisplayName("Create Product with null description should throw")
    void createProduct_withNullDescription_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product(null, BigDecimal.valueOf(99.99), 10)
        );
        assertEquals("Descrição vazia ou inexistente", ex.getMessage());
    }

    @Test
    @DisplayName("Create Product with extra spaces in description should normalize")
    void createProduct_descriptionWithExtraSpaces_shouldNormalize() {
        Product product = new Product(" Valid  Description ", BigDecimal.valueOf(99.99), 10);
        assertEquals("Valid Description", product.getDescription());
    }

    @Test
    @DisplayName("Create Product with description exceeding max length should throw")
    void createProduct_descriptionExceedsMaxLength_shouldThrow() {
        String longDesc = "a".repeat(201);
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product(longDesc, BigDecimal.valueOf(99.99), 10)
        );
        assertEquals("Descrição excede " + Product.getDescriptionMaxLength() + " caracteres", ex.getMessage());
    }

    // PRICE

    @Test
    @DisplayName("Create Product with null price should throw")
    void createProduct_withNullPrice_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", null, 10)
        );
        assertEquals("Valor não pode ser nulo.", ex.getMessage());
    }

    @Test
    @DisplayName("Create Product with zero price should throw")
    void createProduct_withZeroPrice_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", BigDecimal.ZERO, 10)
        );
        assertEquals("O valor deve ser maior que zero", ex.getMessage());
    }

    @Test
    @DisplayName("Create Product with negative price should throw")
    void createProduct_withNegativePrice_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", BigDecimal.valueOf(-10), 10)
        );
        assertEquals("O valor deve ser maior que zero", ex.getMessage());
    }

    @Test
    @DisplayName("Create Product with more than two decimals should round half up")
    void createProduct_priceWithMoreThanTwoDecimals_shouldRoundHalfUp() {
        Product product = new Product("Valid Description", new BigDecimal("10.555"), 10);
        assertEquals(new BigDecimal("10.56"), product.getPrice());
    }

    @Test
    @DisplayName("Create Product with one decimal should normalize to two decimals")
    void createProduct_priceWithOneDecimal_shouldNormalizeToTwoDecimals() {
        Product product = new Product("Valid Description", new BigDecimal("10.5"), 10);
        assertEquals(new BigDecimal("10.50"), product.getPrice());
    }

    // STOCK

    @Test
    @DisplayName("Create Product with negative stock should throw")
    void createProduct_withNegativeStock_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", BigDecimal.valueOf(10), -1)
        );
        assertEquals("O estoque não pode ser negativo", ex.getMessage());
    }

    @Test
    @DisplayName("Create Product with zero stock should succeed")
    void createProduct_withZeroStock_shouldSucceed() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 0);
        assertEquals(0, product.getStockQuantity());
    }

    @Test
    @DisplayName("Add stock with valid amount should increase stock")
    void addStock_withValidAmount_shouldIncreaseStock() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);
        product.addStock(3);
        assertEquals(8, product.getStockQuantity());
    }

    @Test
    @DisplayName("Add stock with zero or negative amount should throw")
    void addStock_withZeroOrNegativeAmount_shouldThrow() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);
        assertThrows(DomainValidationException.class, () -> product.addStock(0));
        assertThrows(DomainValidationException.class, () -> product.addStock(-1));
    }

    @Test
    @DisplayName("Remove stock with valid amount should decrease stock")
    void removeStock_withValidAmount_shouldDecreaseStock() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);
        product.removeStock(2);
        assertEquals(3, product.getStockQuantity());
    }

    @Test
    @DisplayName("Remove stock with amount greater than stock should throw")
    void removeStock_withAmountGreaterThanStock_shouldThrow() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);
        assertThrows(DomainValidationException.class, () -> product.removeStock(6));
    }

    @Test
    @DisplayName("Remove stock with zero or negative amount should throw")
    void removeStock_withZeroOrNegativeAmount_shouldThrow() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);
        assertThrows(DomainValidationException.class, () -> product.removeStock(0));
        assertThrows(DomainValidationException.class, () -> product.removeStock(-1));
    }

    @Test
    @DisplayName("isStockEmpty should return true when stock is zero")
    void isStockEmpty_shouldReturnTrueWhenStockIsZero() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 0);
        assertTrue(product.isStockEmpty());
    }

    @Test
    @DisplayName("isStockEmpty should return false when stock is positive")
    void isStockEmpty_shouldReturnFalseWhenPositiveStock() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 1);
        assertFalse(product.isStockEmpty());
    }

    @Test
    @DisplayName("hasStock should return correctly")
    void hasStock_shouldReturnCorrectly() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);
        assertTrue(product.hasStock(3));
        assertTrue(product.hasStock(5));
        assertFalse(product.hasStock(6));
    }

    // ID

    @Test
    @DisplayName("Set ID twice should throw")
    void setIdTwice_shouldThrow() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(99.99), 10);
        product.setId(1L);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> product.setId(2L)
        );
        assertEquals("ID já definido. Não é permitido alterar.", ex.getMessage());
    }
}