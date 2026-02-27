package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.dto.request.CreateOrderRequest;
import br.com.manoel.ordermanagement.dto.response.OrderResponse;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Order;
import br.com.manoel.ordermanagement.model.OrderItem;
import br.com.manoel.ordermanagement.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderService(OrderRepository repository, CustomerService customerService, ProductService productService) {
        this.repository = repository;
        this.customerService = customerService;
        this.productService = productService;
    }

    // CREATE ORDER
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        if (request.customerId() == null) {
            throw new DomainValidationException("ID de cliente não pode ser nulo");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new DomainValidationException("Pedido precisa ter pelo menos 1 item");
        }

        customerService.findById(request.customerId());

        Map<Long, CreateOrderRequest.OrderItemRequest> mergedByProduct = getLongOrderItemRequestMap(request);

        List<OrderItem> items = mergedByProduct.values().stream()
                .map(i -> {
                    var product = productService.findById(i.productId());

                    OrderItem item = new OrderItem(
                            product.getId(),
                            i.quantity(),
                            product.getPrice(),
                            i.discount() != null ? i.discount() : BigDecimal.ZERO
                    );

                    productService.decrementStock(product.getId(), i.quantity());

                    return item;
                })
                .toList();

        Order order = new Order(request.customerId(), Instant.now(), null);
        items.forEach(order::addItem);

        repository.save(order);

        return toResponse(order);
    }

    private static @NonNull Map<Long, CreateOrderRequest.OrderItemRequest> getLongOrderItemRequestMap(CreateOrderRequest request) {
        Map<Long, CreateOrderRequest.OrderItemRequest> mergedByProduct = new LinkedHashMap<>();

        for (var i : request.items()) {
            if (i == null) {
                throw new DomainValidationException("Item não pode ser nulo");
            }
            if (i.productId() == null) {
                throw new DomainValidationException("Produto não pode ser nulo");
            }
            if (i.quantity() <= 0) throw new DomainValidationException("Quantidade deve ser maior que zero");

            mergedByProduct.merge(
                    i.productId(),
                    i,
                    (a, b) -> new CreateOrderRequest.OrderItemRequest(
                            a.productId(),
                            a.quantity() + b.quantity(),
                            maxDiscount(a.discount(), b.discount())
                    )
            );
        }
        return mergedByProduct;
    }

    private static BigDecimal maxDiscount(BigDecimal a, BigDecimal b) {
        BigDecimal da = (a != null) ? a : BigDecimal.ZERO;
        BigDecimal db = (b != null) ? b : BigDecimal.ZERO;
        return da.max(db);
    }

    // READ OPERATIONS
    public OrderResponse findResponseById(Long id) {
        return toResponse(findById(id));
    }

    public List<OrderResponse> findAllResponses() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> findByCustomerIdResponse(Long customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> findByProductIdResponse(Long productId) {
        return repository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> findByPeriodResponse(Instant start, Instant end) {

        if (start == null || end == null) {
            throw new DomainValidationException("Data inicial e final não podem ser nulas");
        }

        if (start.isAfter(end)) {
            throw new DomainValidationException("Data inicial não pode ser posterior à data final");
        }

        return repository.findByPeriod(start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BigDecimal findTotalAmountByCustomer(Long customerId) {
        if (customerId == null) {
            throw new DomainValidationException("customerId não pode ser nulo");
        }
        return repository.findTotalAmountByCustomer(customerId);
    }

    private Order findById(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }

    private OrderResponse toResponse(Order order) {

        List<OrderResponse.OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getProductId(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getDiscount(),
                        item.getTotalPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                items,
                order.getTotalAmount(),
                order.getOrderDate()
        );
    }
}