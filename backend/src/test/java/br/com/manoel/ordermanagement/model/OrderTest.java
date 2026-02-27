package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static br.com.manoel.ordermanagement.model.MoneyConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    // CREATE ORDER

    @Test
    @DisplayName("Create order with valid customerId should succeed")
    void createOrder_withValidCustomerId_shouldSucceed() {
        Order order = new Order(1L);

        assertEquals(1L, order.getCustomerId());
        assertNotNull(order.getOrderDate());
        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING), order.getTotalAmount());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Create order with null customerId should throw")
    void createOrder_withNullCustomer_shouldThrow() {
        assertThrows(DomainValidationException.class, () -> new Order(null));
    }

    @Test
    @DisplayName("Create order with null orderDate should default to now and calculate total")
    void createOrder_withNullOrderDate_shouldDefaultToNow() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(101L, 2, new BigDecimal("100.00"), new BigDecimal("10.00"))); // total 190

        Order order = new Order(1L, null, items);

        assertNotNull(order.getOrderDate());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("190.00").setScale(MONEY_SCALE, MONEY_ROUNDING), order.getTotalAmount());
    }

    @Test
    @DisplayName("Create order with custom date and items should calculate total correctly")
    void createOrder_withCustomDateAndItems_shouldCalculateTotal() {

        Instant date = Instant.parse("2024-01-01T00:00:00Z");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(101L, 2, new BigDecimal("100.00"), new BigDecimal("10.00"))); // total 190

        Order order = new Order(1L, date, items);

        assertEquals(date, order.getOrderDate());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("190.00").setScale(MONEY_SCALE, MONEY_ROUNDING), order.getTotalAmount());
    }

    @Test
    @DisplayName("Create order with duplicated products in items should merge and calculate total")
    void createOrder_withDuplicatedProducts_shouldMerge() {
        Instant date = Instant.parse("2024-01-01T00:00:00Z");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(101L, 2, new BigDecimal("100.00"), new BigDecimal("5.00")));   // total 195
        items.add(new OrderItem(101L, 3, new BigDecimal("100.00"), new BigDecimal("10.00")));  // total 290

        Order order = new Order(1L, date, items);

        assertEquals(1, order.getItems().size());
        OrderItem merged = order.getItems().getFirst();

        assertEquals(101L, merged.getProductId());
        assertEquals(5, merged.getQuantity());
        assertEquals(new BigDecimal("10.00").setScale(MONEY_SCALE, MONEY_ROUNDING), merged.getDiscount());
        assertEquals(new BigDecimal("490.00").setScale(MONEY_SCALE, MONEY_ROUNDING), merged.getTotalPrice());
        assertEquals(merged.getTotalPrice(), order.getTotalAmount());
    }

    // ADD ITEM

    @Test
    @DisplayName("Add item should increase totalAmount correctly")
    void addItem_shouldRecalculateTotal() {

        Order order = new Order(1L);

        order.addItem(new OrderItem(
                101L,
                2,
                new BigDecimal("100.00"),
                new BigDecimal("10.00")
        ));

        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("190.00").setScale(MONEY_SCALE, MONEY_ROUNDING), order.getTotalAmount());
    }

    @Test
    @DisplayName("Add null item should throw")
    void addItem_null_shouldThrow() {

        Order order = new Order(1L);

        assertThrows(DomainValidationException.class, () -> order.addItem(null));
    }

    @Test
    @DisplayName("Add same product should merge quantities and keep highest discount")
    void addItem_sameProduct_shouldMerge() {
        Order order = getOrderWithMergedProduct();

        assertEquals(1, order.getItems().size());

        OrderItem merged = order.getItems().getFirst();

        assertEquals(5, merged.getQuantity());
        assertEquals(new BigDecimal("10.00").setScale(MONEY_SCALE, MONEY_ROUNDING), merged.getDiscount());
        assertEquals(new BigDecimal("490.00").setScale(MONEY_SCALE, MONEY_ROUNDING), merged.getTotalPrice());
        assertEquals(merged.getTotalPrice(), order.getTotalAmount());
    }

    private static @NonNull Order getOrderWithMergedProduct() {
        Order order = new Order(1L);

        OrderItem item1 = new OrderItem(
                101L,
                2,
                new BigDecimal("100.00"),
                new BigDecimal("5.00")
        );

        OrderItem item2 = new OrderItem(
                101L,
                3,
                new BigDecimal("100.00"),
                new BigDecimal("10.00")
        );

        order.addItem(item1);
        order.addItem(item2);
        return order;
    }

    // CONFIRM ORDER

    @Test
    @DisplayName("Adding item after setting ID should throw (order already persisted)")
    void addItem_afterPersist_shouldThrow() {
        Order order = new Order(1L);

        order.addItem(new OrderItem(101L, 1, new BigDecimal("50.00"), BigDecimal.ZERO));
        order.setId(1L);

        assertThrows(DomainValidationException.class, () ->
                order.addItem(new OrderItem(102L, 1, new BigDecimal("10.00"), BigDecimal.ZERO))
        );
    }

    // GET ITEMS

    @Test
    @DisplayName("getItems should return unmodifiable list")
    void getItems_shouldBeUnmodifiable() {

        Order order = new Order(1L);

        order.addItem(new OrderItem(
                101L,
                1,
                new BigDecimal("50.00"),
                BigDecimal.ZERO
        ));

        List<OrderItem> items = order.getItems();

        assertThrows(UnsupportedOperationException.class, () ->
                items.add(new OrderItem(102L, 1, new BigDecimal("10.00"), BigDecimal.ZERO))
        );
    }

    // setId(...)

    @Test
    @DisplayName("Set ID twice should throw")
    void setId_twice_shouldThrow() {

        Order order = new Order(1L);

        order.setId(1L);
        assertEquals(1L, order.getId());

        assertThrows(IllegalStateException.class, () -> order.setId(2L));
    }

    // EQUALS & HASHCODE

    @Test
    @DisplayName("Orders with same ID should be equal")
    void equals_sameId_shouldBeEqual() {

        Order o1 = new Order(1L);
        Order o2 = new Order(1L);

        o1.setId(10L);
        o2.setId(10L);

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
    }

    @Test
    @DisplayName("Orders with different IDs should not be equal")
    void equals_differentId_shouldNotBeEqual() {

        Order o1 = new Order(1L);
        Order o2 = new Order(1L);

        o1.setId(1L);
        o2.setId(2L);

        assertNotEquals(o1, o2);
    }

    @Test
    @DisplayName("Order with null ID should not equal order with ID")
    void equals_nullId_shouldNotBeEqual() {

        Order orderWithId = new Order(1L);
        Order orderWithoutId = new Order(1L);

        orderWithId.setId(1L);

        assertNotEquals(orderWithId, orderWithoutId);
    }
}