package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.dto.request.CreateOrderRequest;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Order;
import br.com.manoel.ordermanagement.model.OrderItem;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static br.com.manoel.ordermanagement.model.MoneyConstants.MONEY_ROUNDING;
import static br.com.manoel.ordermanagement.model.MoneyConstants.MONEY_SCALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository repository;
    private CustomerService customerService;
    private ProductService productService;
    private OrderService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrderRepository.class);
        customerService = mock(CustomerService.class);
        productService = mock(ProductService.class);

        service = new OrderService(repository, customerService, productService);
    }

    // =========================
    // CREATE
    // =========================

    @Test
    @DisplayName("Create order should return Order and call dependencies")
    void create_shouldReturnOrder() {

        Long customerId = 1L;
        Long productId = 10L;

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderItemRequest(
                        productId,
                        2,
                        BigDecimal.ZERO
                ))
        );

        Product product = new Product("Produto", BigDecimal.valueOf(100), 10);
        product.setId(productId);

        when(customerService.findById(customerId)).thenReturn(null);
        when(productService.findById(productId)).thenReturn(product);

        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            if (o.getId() == null) o.setId(1L);
            return o;
        });

        Order order = service.create(request);

        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertNotNull(order.getOrderDate());
        assertEquals(1, order.getItems().size());

        verify(customerService).findById(customerId);
        verify(productService).findById(productId);
        verify(productService).decrementStock(productId, 2);
        verify(repository).save(any(Order.class));
    }

    @Test
    @DisplayName("Create order should merge items by productId (sum qty and call decrementStock once)")
    void create_shouldMergeItemsByProductId() {

        Long customerId = 1L;
        Long productId = 10L;

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(
                        new CreateOrderRequest.OrderItemRequest(productId, 2, BigDecimal.valueOf(5)),
                        new CreateOrderRequest.OrderItemRequest(productId, 3, BigDecimal.valueOf(1))
                )
        );

        Product product = new Product("Produto", BigDecimal.valueOf(100), 10);
        product.setId(productId);

        when(customerService.findById(customerId)).thenReturn(null);
        when(productService.findById(productId)).thenReturn(product);

        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            if (o.getId() == null) o.setId(1L);
            return o;
        });

        Order order = service.create(request);

        assertEquals(1, order.getItems().size(), "Itens devem ser mesclados por productId");

        OrderItem item = order.getItems().getFirst();
        assertEquals(productId, item.getProductId());
        assertEquals(5, item.getQuantity(), "Quantidade deve ser a soma");
        assertEquals(BigDecimal.valueOf(5).setScale(MONEY_SCALE, MONEY_ROUNDING), item.getDiscount(), "Desconto deve ser o maior (max)");

        verify(productService, times(1)).decrementStock(productId, 5);
        verify(productService, times(1)).findById(productId);
        verify(repository).save(any(Order.class));
    }

    @Test
    @DisplayName("Create order should default null discount to zero")
    void create_withNullDiscount_shouldDefaultToZero() {

        Long customerId = 1L;
        Long productId = 10L;

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderItemRequest(
                        productId,
                        2,
                        null
                ))
        );

        Product product = new Product("Produto", BigDecimal.valueOf(100), 10);
        product.setId(productId);

        when(customerService.findById(customerId)).thenReturn(null);
        when(productService.findById(productId)).thenReturn(product);

        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            if (o.getId() == null) o.setId(1L);
            return o;
        });

        Order order = service.create(request);

        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING), order.getItems().getFirst().getDiscount());

        verify(productService).decrementStock(productId, 2);
    }

    // =========================
    // FIND BY ID
    // =========================

    @Test
    void findById_existing_shouldReturnOrder() {

        Order order = new Order(1L, Instant.parse("2026-02-27T10:00:00Z"),
                List.of(new OrderItem(10L, 2, BigDecimal.TEN, BigDecimal.ZERO)));
        order.setId(99L);

        when(repository.findById(99L)).thenReturn(Optional.of(order));

        Order result = service.findById(99L);

        assertEquals(99L, result.getId());
    }

    @Test
    void findById_nonExisting_shouldThrow() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));
    }

    // =========================
    // FIND ALL
    // =========================

    @Test
    void findAll_shouldReturnList() {

        Order order = new Order(1L, Instant.parse("2026-02-27T10:00:00Z"),
                List.of(new OrderItem(10L, 2, BigDecimal.TEN, BigDecimal.ZERO)));

        when(repository.findAll()).thenReturn(List.of(order));

        List<Order> orders = service.findAll();

        assertEquals(1, orders.size());
    }

    // =========================
    // TOTAL AMOUNT
    // =========================

    @Test
    void findTotalAmountByCustomer_shouldReturnValue() {

        when(repository.findTotalAmountByCustomer(1L))
                .thenReturn(BigDecimal.valueOf(500));

        BigDecimal result = service.findTotalAmountByCustomer(1L);

        assertEquals(BigDecimal.valueOf(500), result);
    }

    @Test
    void findTotalAmountByCustomer_null_shouldThrow() {
        assertThrows(DomainValidationException.class,
                () -> service.findTotalAmountByCustomer(null));
    }

    // =========================
    // FIND BY PERIOD
    // =========================

    @Test
    void findByPeriod_valid_shouldReturnList() {

        Instant start = Instant.parse("2026-02-27T10:00:00Z");
        Instant end = Instant.parse("2026-02-27T11:00:00Z");

        Order order = new Order(1L, Instant.parse("2026-02-27T10:30:00Z"),
                List.of(new OrderItem(10L, 2, BigDecimal.TEN, BigDecimal.ZERO)));

        when(repository.findByPeriod(start, end)).thenReturn(List.of(order));

        List<Order> orders = service.findByPeriod(start, end);

        assertEquals(1, orders.size());
    }

    @Test
    void findByPeriod_invalidDates_shouldThrow() {

        Instant now = Instant.parse("2026-02-27T10:00:00Z");

        assertThrows(DomainValidationException.class,
                () -> service.findByPeriod(null, now));

        assertThrows(DomainValidationException.class,
                () -> service.findByPeriod(now, null));

        assertThrows(DomainValidationException.class,
                () -> service.findByPeriod(now.plusSeconds(1000), now));
    }
}