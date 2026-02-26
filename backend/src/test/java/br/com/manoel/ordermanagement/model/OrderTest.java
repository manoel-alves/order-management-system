package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static br.com.manoel.ordermanagement.model.MoneyConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    // Mocks
    private Product createProduct(BigDecimal price, int stock) {
        return new Product("Test Product", price, stock);
    }

    private Customer createCustomer() {
        return new Customer("John Doe", "john@example.com");
    }

    // CREATE ORDER

    @Test
    @DisplayName("Create order with valid customer should succeed")
    void createOrder_withValidCustomer_shouldSucceed() {
        Customer customer = createCustomer();
        Order order = new Order(customer);

        assertEquals(customer, order.getCustomer());
        assertNotNull(order.getOrderDate());
        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING), order.getTotalAmount());
        assertFalse(order.isConfirmed());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Create order with null customer should throw")
    void createOrder_withNullCustomer_shouldThrow() {
        assertThrows(DomainValidationException.class, () -> new Order(null));
    }

    @Test
    @DisplayName("Create order with custom date should respect value")
    void createOrder_withCustomDate_shouldRespectValue() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Customer customer = createCustomer();
        Order order = new Order(customer, now);

        assertEquals(now, order.getOrderDate());
    }

    // ADD ITEM -> addItem(...)

    @Test
    @DisplayName("Add item with valid product and quantity should succeed")
    void addItem_withValidData_shouldSucceed() {
        Product product = createProduct(new BigDecimal("100.00"), 10);
        Order order = new Order(createCustomer());

        order.addItem(product, 2, new BigDecimal("10.00"));

        List<OrderItem> items = order.getItems();
        assertEquals(1, items.size());

        OrderItem item = items.getFirst();
        assertEquals(product, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("10.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount());
        assertEquals(new BigDecimal("100.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getUnitPrice());
        assertEquals(new BigDecimal("190.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getTotalPrice());

        assertEquals(item.getTotalPrice(), order.getTotalAmount());
    }

    @Test
    @DisplayName("Add item with null product should throw")
    void addItem_withNullProduct_shouldThrow() {
        Order order = new Order(createCustomer());
        assertThrows(DomainValidationException.class, () -> order.addItem(null, 1));
    }

    @Test
    @DisplayName("Add item with zero or negative quantity should throw")
    void addItem_withInvalidQuantity_shouldThrow() {
        Product product = createProduct(new BigDecimal("10.00"), 10);
        Order order = new Order(createCustomer());

        assertThrows(DomainValidationException.class, () -> order.addItem(product, 0));
        assertThrows(DomainValidationException.class, () -> order.addItem(product, -1));
    }

    @Test
    @DisplayName("Add item exceeding stock should throw")
    void addItem_exceedingStock_shouldThrow() {
        Product product = createProduct(new BigDecimal("10.00"), 2);
        Order order = new Order(createCustomer());

        assertThrows(DomainValidationException.class, () -> order.addItem(product, 3));
    }

    @Test
    @DisplayName("Add same product should consolidate quantity and keep highest discount")
    void addItem_sameProduct_shouldConsolidateQuantityAndDiscount() {
        Product product = createProduct(new BigDecimal("50.00"), 10);
        Order order = new Order(createCustomer());

        order.addItem(product, 2, new BigDecimal("5.00"));
        order.addItem(product, 3, new BigDecimal("10.00")); // higher discount

        assertEquals(1, order.getItems().size());

        OrderItem item = order.getItems().getFirst();
        assertEquals(5, item.getQuantity());
        assertEquals(new BigDecimal("10.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount());
        assertEquals(new BigDecimal("50.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getUnitPrice());
        assertEquals(new BigDecimal("240.00").setScale(MONEY_SCALE, MONEY_ROUNDING), item.getTotalPrice());

        assertEquals(item.getTotalPrice(), order.getTotalAmount());
    }

    // CONFIRM ORDER -> confirmOrder()

    @Test
    @DisplayName("Confirm order without items should throw")
    void confirmOrder_withoutItems_shouldThrow() {
        Order order = new Order(createCustomer());
        assertThrows(DomainValidationException.class, order::confirmOrder);
    }

    @Test
    @DisplayName("Confirm order should reduce stock and mark confirmed")
    void confirmOrder_withItems_shouldReduceStockAndMarkConfirmed() {
        Product product = createProduct(new BigDecimal("50.00"), 10);
        Order order = new Order(createCustomer());

        order.addItem(product, 3);
        order.confirmOrder();

        assertEquals(7, product.getStockQuantity());
        assertTrue(order.isConfirmed());
    }

    @Test
    @DisplayName("Adding item to confirmed order should throw")
    void addItem_toConfirmedOrder_shouldThrow() {
        Product product = createProduct(new BigDecimal("50.00"), 10);
        Order order = new Order(createCustomer());

        order.addItem(product, 2);
        order.confirmOrder();

        assertThrows(DomainValidationException.class, () -> order.addItem(product, 1));
    }

    @Test
    @DisplayName("Confirming order twice should throw")
    void confirmOrder_twice_shouldThrow() {
        Product product = createProduct(new BigDecimal("50.00"), 10);
        Order order = new Order(createCustomer());

        order.addItem(product, 2);
        order.confirmOrder();

        assertThrows(DomainValidationException.class, order::confirmOrder);
    }

    // GET ITEMS -> getItems()

    @Test
    @DisplayName("getItems should return unmodifiable list")
    void getItems_shouldReturnUnmodifiableList() {
        Product product = createProduct(new BigDecimal("50.00"), 10);
        Order order = new Order(createCustomer());

        order.addItem(product, 1);

        List<OrderItem> items = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(new OrderItem(product, 1, BigDecimal.ZERO)));
    }

    // setId(...)

    @Test
    @DisplayName("Set ID twice should throw")
    void setId_twice_shouldThrow() {
        Order order = new Order(createCustomer());
        order.setId(1L);
        assertEquals(1L, order.getId());

        assertThrows(IllegalStateException.class, () -> order.setId(2L));
    }

    // EQUALS AND HASHCODE

    @Test
    @DisplayName("Orders with same ID should be equal")
    void equals_sameId_shouldBeEqual() {
        Order order1 = new Order(createCustomer());
        Order order2 = new Order(createCustomer());

        order1.setId(1L);
        order2.setId(1L);

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    @DisplayName("Orders with different IDs should not be equal")
    void equals_differentId_shouldNotBeEqual() {
        Order order1 = new Order(createCustomer());
        Order order2 = new Order(createCustomer());

        order1.setId(1L);
        order2.setId(2L);

        assertNotEquals(order1, order2);
    }

    @Test
    @DisplayName("Order with null ID should not equal order with set ID")
    void equals_nullId_shouldNotBeEqual() {
        Order orderWithId = new Order(createCustomer());
        Order orderWithoutId = new Order(createCustomer());

        orderWithId.setId(1L);

        assertNotEquals(orderWithId, orderWithoutId);
    }
}