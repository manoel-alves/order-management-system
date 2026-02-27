package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.dto.request.CreateOrderRequest;
import br.com.manoel.ordermanagement.dto.response.OrderResponse;
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
    @DisplayName("Create order should return OrderResponse")
    void create_shouldReturnOrderResponse() {

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

            if (o.getId() == null) {
                o.setId(1L);
            }

            return o;
        });

        OrderResponse response = service.create(request);

        assertNotNull(response);
        assertEquals(customerId, response.customerId());
        assertEquals(1, response.items().size());

        verify(repository).save(any(Order.class));
    }

    @Test
    void create_withNullCustomer_shouldThrow() {
        CreateOrderRequest request = new CreateOrderRequest(null, List.of());

        assertThrows(DomainValidationException.class,
                () -> service.create(request));
    }

    // =========================
    // FIND RESPONSE BY ID
    // =========================

    @Test
    void findResponseById_existing_shouldReturnResponse() {

        Order order = new Order(1L, Instant.now(),
                List.of(new OrderItem(10L, 2, BigDecimal.TEN, BigDecimal.ZERO)));
        order.setId(99L);

        when(repository.findById(99L)).thenReturn(Optional.of(order));

        OrderResponse response = service.findResponseById(99L);

        assertEquals(99L, response.id());
    }

    @Test
    void findResponseById_nonExisting_shouldThrow() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findResponseById(1L));
    }

    // =========================
    // FIND ALL
    // =========================

    @Test
    void findAllResponses_shouldReturnList() {

        Order order = new Order(1L, Instant.now(),
                List.of(new OrderItem(10L, 2, BigDecimal.TEN, BigDecimal.ZERO)));

        when(repository.findAll()).thenReturn(List.of(order));

        List<OrderResponse> responses = service.findAllResponses();

        assertEquals(1, responses.size());
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
    void findByPeriodResponse_valid_shouldReturnList() {

        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);

        Order order = new Order(1L, Instant.now(),
                List.of(new OrderItem(10L, 2, BigDecimal.TEN, BigDecimal.ZERO)));

        when(repository.findByPeriod(start, end)).thenReturn(List.of(order));

        List<OrderResponse> responses = service.findByPeriodResponse(start, end);

        assertEquals(1, responses.size());
    }

    @Test
    void findByPeriodResponse_invalidDates_shouldThrow() {

        Instant now = Instant.now();

        assertThrows(DomainValidationException.class,
                () -> service.findByPeriodResponse(null, now));

        assertThrows(DomainValidationException.class,
                () -> service.findByPeriodResponse(now, null));

        assertThrows(DomainValidationException.class,
                () -> service.findByPeriodResponse(now.plusSeconds(1000), now));
    }
}