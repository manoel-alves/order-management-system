package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.manoel.ordermanagement.model.MoneyConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    // CREATE OrderItem

    @Test
    @DisplayName("Create OrderItem with valid data should succeed")
    void createOrderItem_withValidData_shouldSucceed() {

        OrderItem item = new OrderItem(
                1L,
                2,
                new BigDecimal("100.00"),
                new BigDecimal("10.00")
        );

        assertEquals(1L, item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("100.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getUnitPrice());
        assertEquals(new BigDecimal("10.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount());

        // subtotal = 100 * 2 = 200
        // total = 200 - 10 = 190
        assertEquals(new BigDecimal("190.00").setScale(MONEY_SCALE, MONEY_ROUNDING),
                item.getTotalPrice());
    }

    @Test
    @DisplayName("Create OrderItem with null discount should assume zero")
    void createOrderItem_withNullDiscount_shouldAssumeZero() {

        OrderItem item = new OrderItem(
                1L,
                1,
                new BigDecimal("50.00"),
                null
        );

        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING),
                item.getDiscount());

        assertEquals(new BigDecimal("50.00").setScale(MONEY_SCALE, MONEY_ROUNDING),
                item.getTotalPrice());
    }

    @Test
    @DisplayName("Create OrderItem with zero or negative quantity should throw")
    void createOrderItem_withInvalidQuantity_shouldThrow() {

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(1L, 0, new BigDecimal("10.00"), BigDecimal.ZERO));

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(1L, -1, new BigDecimal("10.00"), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Create OrderItem with null productId should throw")
    void createOrderItem_withNullProduct_shouldThrow() {

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(null, 1, new BigDecimal("10.00"), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Create OrderItem with null or negative unitPrice should throw")
    void createOrderItem_withInvalidUnitPrice_shouldThrow() {

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(1L, 1, null, BigDecimal.ZERO));

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(1L, 1, new BigDecimal("-1.00"), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Create OrderItem with negative discount should throw")
    void createOrderItem_withNegativeDiscount_shouldThrow() {

        assertThrows(DomainValidationException.class,
                () -> new OrderItem(1L, 1, new BigDecimal("100.00"), new BigDecimal("-1.00")));
    }

    @Test
    @DisplayName("Create OrderItem with discount greater than subtotal should throw")
    void createOrderItem_withDiscountGreaterThanSubtotal_shouldThrow() {

        // subtotal = 100
        assertThrows(DomainValidationException.class,
                () -> new OrderItem(1L, 1, new BigDecimal("100.00"), new BigDecimal("150.00")));
    }

    // MONEY NORMALIZATION

    @Test
    @DisplayName("OrderItem should normalize unitPrice scale")
    void orderItem_shouldNormalizeUnitPriceScale() {

        OrderItem item = new OrderItem(
                1L,
                1,
                new BigDecimal("10.555"),
                BigDecimal.ZERO
        );

        assertEquals(new BigDecimal("10.56").setScale(MONEY_SCALE, MONEY_ROUNDING),
                item.getUnitPrice());
    }

    @Test
    @DisplayName("OrderItem should normalize discount scale")
    void orderItem_shouldNormalizeDiscountScale() {

        OrderItem item = new OrderItem(
                1L,
                1,
                new BigDecimal("100.00"),
                new BigDecimal("10.555")
        );

        assertEquals(2, item.getDiscount().scale());
        assertEquals(new BigDecimal("10.56").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount());
    }

    @Test
    @DisplayName("OrderItem should calculate totalPrice correctly with rounding")
    void orderItem_totalPriceCalculation_shouldRoundCorrectly() {

        OrderItem item = new OrderItem(
                1L,
                3,
                new BigDecimal("10.555"),
                new BigDecimal("5.00")
        );

        // unitPrice rounded = 10.56
        assertEquals(new BigDecimal("10.56").setScale(MONEY_SCALE, MONEY_ROUNDING),
                item.getUnitPrice());

        // subtotal = 10.56 * 3 = 31.68
        // total = 31.68 - 5 = 26.68
        assertEquals(new BigDecimal("26.68").setScale(MONEY_SCALE, MONEY_ROUNDING),
                item.getTotalPrice());
    }

    // EQUALS & HASHCODE

    @Test
    @DisplayName("Equals should be based on productId")
    void equals_shouldBeBasedOnProduct() {

        OrderItem item1 = new OrderItem(1L, 1, new BigDecimal("100.00"), BigDecimal.ZERO);
        OrderItem item2 = new OrderItem(1L, 5, new BigDecimal("20.00"), BigDecimal.ZERO);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("Items with different productIds should not be equal")
    void items_withDifferentProducts_shouldNotBeEqual() {

        OrderItem item1 = new OrderItem(1L, 1, new BigDecimal("100.00"), BigDecimal.ZERO);
        OrderItem item2 = new OrderItem(2L, 1, new BigDecimal("100.00"), BigDecimal.ZERO);

        assertNotEquals(item1, item2);
    }

    // setId(...)

    @Test
    @DisplayName("Set ID twice should throw")
    void setId_twice_shouldThrow() {

        OrderItem item = new OrderItem(1L, 1, new BigDecimal("10.00"), BigDecimal.ZERO);

        item.setId(1L);
        assertEquals(1L, item.getId());

        assertThrows(IllegalStateException.class,
                () -> item.setId(2L));
    }
}