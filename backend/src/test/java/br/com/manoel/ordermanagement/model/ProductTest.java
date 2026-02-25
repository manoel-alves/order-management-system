package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    // Test Instant injection
    @Test
    void createProduct_withCustomCreatedAt_shouldRespectValue() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");

        Product product = new Product("Valid Description", BigDecimal.valueOf(99.99), 10, now);

        assertEquals(now, product.getCreatedAt());
    }

    // Test product creation
    @Test
    void createProduct_withValidData_shouldSucceed() {
        Product product = new Product("Valid Product - Description", BigDecimal.valueOf(99.99), 10);

        assertNotNull(product.getDescription());
        assertNotNull(product.getPrice());
        assertNotNull(product.getCreatedAt());
    }

    // Test blank description
    @Test
    void createProduct_withEmptyDescription_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("", BigDecimal.valueOf(99.99), 10)
        );

        assertEquals("Descrição vazia ou inexistente", ex.getMessage());
    }

    // Test null description
    @Test
    void createProduct_withNullDescription_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product(null, BigDecimal.valueOf(99.99), 10)
        );

        assertEquals("Descrição vazia ou inexistente", ex.getMessage());
    }

    // Test description normalization
    @Test
    void createProduct_descriptionWithExtraSpaces_shouldNormalize() {
        Product product = new Product(" Valid  Description ", BigDecimal.valueOf(99.99), 10);

        assertEquals("Valid Description", product.getDescription());
    }

    // Test description that exceeds max length
    @Test
    void createProduct_descriptionExceedsMaxLength_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("a".repeat(201), BigDecimal.valueOf(99.99), 10)
        );
        assertEquals("Descrição excede " + Product.getDescriptionMaxLength() + " caracteres", ex.getMessage());
    }

    // Test null price
    @Test
    void createProduct_withNullPrice_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", null, 10)
        );

        assertEquals("Valor não pode ser nulo.", ex.getMessage());
    }

    // Test zero price
    @Test
    void createProduct_withZeroPrice_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", BigDecimal.ZERO, 10)
        );

        assertEquals("O valor deve ser maior que zero", ex.getMessage());
    }

    // Test negative price
    @Test
    void createProduct_withNegativePrice_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", BigDecimal.valueOf(-10), 10)
        );

        assertEquals("O valor deve ser maior que zero", ex.getMessage());
    }

    // Test price rounding
    @Test
    void createProduct_priceWithMoreThanTwoDecimals_shouldRoundHalfUp() {
        Product product = new Product(
                "Valid Description",
                new BigDecimal("10.555"),
                10
        );

        assertEquals(new BigDecimal("10.56"), product.getPrice());
    }

    // Test price scale
    @Test
    void createProduct_priceWithOneDecimal_shouldNormalizeToTwoDecimals() {
        Product product = new Product(
                "Valid Description",
                new BigDecimal("10.5"),
                10
        );

        assertEquals(new BigDecimal("10.50"), product.getPrice());
    }

    // Test negative stock
    @Test
    void createProduct_withNegativeStock_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Product("Valid Description", BigDecimal.valueOf(10), -1)
        );

        assertEquals("O estoque não pode ser negativo", ex.getMessage());
    }

    // Test zero stock initialization
    @Test
    void createProduct_withZeroStock_shouldSucceed() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 0);

        assertEquals(0, product.getStockQuantity());
    }

    // Test addStock increment
    @Test
    void addStock_withValidAmount_shouldIncreaseStock() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);

        product.addStock(3);

        assertEquals(8, product.getStockQuantity());
    }

    // Test addStock with 0 or negative value -> exception
    @Test
    void addStock_withZeroOrNegativeAmount_shouldThrow() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);

        assertThrows(DomainValidationException.class, () -> product.addStock(0));
        assertThrows(DomainValidationException.class, () -> product.addStock(-1));
    }

    // Test removeStock decrement
    @Test
    void removeStock_withValidAmount_shouldDecreaseStock() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);

        product.removeStock(2);

        assertEquals(3, product.getStockQuantity());
    }

    // Test removeStock with amount greater than stock -> exception
    @Test
    void removeStock_withAmountGreaterThanStock_shouldThrow() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);

        assertThrows(
                DomainValidationException.class,
                () -> product.removeStock(6)
        );
    }

    // Test removeStock with 0 or negative value -> exception
    @Test
    void removeStock_withZeroOrNegativeAmount_shouldThrow() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);

        assertThrows(DomainValidationException.class, () -> product.removeStock(0));
        assertThrows(DomainValidationException.class, () -> product.removeStock(-1));
    }

    // Test isStockEmpty when stock is zero
    @Test
    void isStockEmpty_shouldReturnTrueWhenStockIsZero() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 0);

        assertTrue(product.isStockEmpty());
    }

    // Test isStockEmpty when positive stock
    @Test
    void isStockEmpty_shouldReturnFalseWhenPositiveStock() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 1);

        assertFalse(product.isStockEmpty());
    }

    // Test hasStock
    @Test
    void hasStock_shouldReturnCorrectly() {
        Product product = new Product("Valid Description", BigDecimal.valueOf(10), 5);

        assertTrue(product.hasStock(3));
        assertTrue(product.hasStock(5));
        assertFalse(product.hasStock(6));
    }

    // Test reassign id
    @Test
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
