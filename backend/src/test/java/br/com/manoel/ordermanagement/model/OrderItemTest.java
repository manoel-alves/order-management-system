package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.manoel.ordermanagement.model.MoneyConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    // mocks

    private Product createProduct(BigDecimal price) {
        return new Product("Valid Product", price, 10);
    }

    // CREATE OrderItem

    @Test
    @DisplayName("Create OrderItem with valid data should succeed")
    void createOrderItem_withValidData_shouldSucceed() {
        Product product = createProduct(new BigDecimal("100.00"));
        OrderItem item = new OrderItem(product, 2, new BigDecimal("10.00"));

        assertEquals(product, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("10.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount());
        assertEquals(new BigDecimal("100.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getUnitPrice());
        assertEquals(new BigDecimal("190.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getTotalPrice());
    }

    @Test
    @DisplayName("Create OrderItem with null discount should assume zero")
    void createOrderItem_withNullDiscount_shouldAssumeZero() {
        Product product = createProduct(new BigDecimal("50.00"));
        OrderItem item = new OrderItem(product, 1, null);

        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount());
        assertEquals(new BigDecimal("50.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getTotalPrice());
    }

    @Test
    @DisplayName("Create OrderItem with discount zero should calculate total correctly")
    void createOrderItem_withZeroDiscount_shouldCalculateTotalCorrectly() {
        Product product = createProduct(new BigDecimal("20.00"));
        OrderItem item = new OrderItem(product, 3, BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount());
        assertEquals(new BigDecimal("60.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getTotalPrice());
    }

    @Test
    @DisplayName("Create OrderItem with null product should throw")
    void createOrderItem_withNullProduct_shouldThrow() {
        assertThrows(DomainValidationException.class,
                () -> new OrderItem(null, 1, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Create OrderItem with zero or negative quantity should throw")
    void createOrderItem_withInvalidQuantity_shouldThrow() {
        Product product = createProduct(new BigDecimal("10.00"));

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(product, 0, BigDecimal.ZERO));
        assertThrows(DomainValidationException.class,
                () -> new OrderItem(product, -1, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Create OrderItem with negative discount should throw")
    void createOrderItem_withNegativeDiscount_shouldThrow() {
        Product product = createProduct(new BigDecimal("100.00"));

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(product, 1, new BigDecimal("-1.00")));
    }

    @Test
    @DisplayName("Create OrderItem with discount greater than subtotal should throw")
    void createOrderItem_withDiscountGreaterThanSubtotal_shouldThrow() {
        Product product = createProduct(new BigDecimal("100.00"));

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(product, 1, new BigDecimal("150.00")));
    }

    // DISCOUNT AND PRICE NORMALIZATION

    @Test
    @DisplayName("OrderItem should normalize unitPrice scale")
    void orderItem_shouldNormalizeUnitPriceScale() {
        Product product = createProduct(new BigDecimal("10.555"));
        OrderItem item = new OrderItem(product, 1, BigDecimal.ZERO);

        assertEquals(new BigDecimal("10.56").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getUnitPrice());
    }

    @Test
    @DisplayName("OrderItem should normalize discount scale")
    void orderItem_shouldNormalizeDiscountScale() {
        Product product = createProduct(new BigDecimal("100.00"));

        OrderItem item = new OrderItem(product, 1, new BigDecimal("10"));

        assertEquals(2, item.getDiscount().scale());
    }

    @Test
    @DisplayName("OrderItem should calculate totalPrice correctly with rounding")
    void orderItem_totalPriceCalculation_shouldRoundCorrectly() {
        Product product = createProduct(new BigDecimal("10.555")); // more than 2 decimals
        OrderItem item = new OrderItem(product, 3, new BigDecimal("5.00"));

        // unitPrice rounded
        assertEquals(new BigDecimal("10.56").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getUnitPrice());
        // subtotal = 10.56 * 3 = 31.68, discount = 5.00, totalPrice = 26.68
        assertEquals(new BigDecimal("26.68").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getTotalPrice());
    }

    // EQUALS by Product

    @Test
    @DisplayName("Equals should be based on product")
    void equals_shouldBeBasedOnProduct() {
        Product product = createProduct(new BigDecimal("100.00"));

        OrderItem item1 = new OrderItem(product, 1, BigDecimal.ZERO);
        OrderItem item2 = new OrderItem(product, 5, new BigDecimal("20.00"));

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    // ITEMS

    @Test
    @DisplayName("Items with different products should not be equal")
    void items_withDifferentProducts_shouldNotBeEqual() {
        Product product1 = createProduct(new BigDecimal("100.00"));
        Product product2 = createProduct(new BigDecimal("100.00"));

        OrderItem item1 = new OrderItem(product1, 1, BigDecimal.ZERO);
        OrderItem item2 = new OrderItem(product2, 1, BigDecimal.ZERO);

        assertNotEquals(item1, item2);
    }

    // setId(...)

    @Test
    @DisplayName("Set ID twice should throw")
    void setId_twice_shouldThrow() {
        Product product = createProduct(new BigDecimal("10.00"));
        OrderItem item = new OrderItem(product, 1, BigDecimal.ZERO);

        item.setId(1L);
        assertEquals(1L, item.getId());

        assertThrows(IllegalStateException.class,
                () -> item.setId(2L));
    }
}