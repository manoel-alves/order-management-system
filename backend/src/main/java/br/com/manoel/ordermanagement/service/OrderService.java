package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.dto.request.CreateOrderRequest;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Order;
import br.com.manoel.ordermanagement.model.OrderItem;
import br.com.manoel.ordermanagement.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public OrderService(
            OrderRepository repository,
            CustomerService customerService,
            ProductService productService
    ) {
        this.repository = repository;
        this.customerService = customerService;
        this.productService = productService;
    }

    // CREATE ORDER
    @Transactional
    public Order create(CreateOrderRequest request) {

        customerService.findById(request.customerId());

        Map<Long, CreateOrderRequest.OrderItemRequest> mergedByProduct = mergeItemsByProduct(request.items());

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

        return repository.save(order);
    }

    private static Map<Long, CreateOrderRequest.OrderItemRequest> mergeItemsByProduct(
            List<CreateOrderRequest.OrderItemRequest> items
    ) {
        Map<Long, CreateOrderRequest.OrderItemRequest> merged = new LinkedHashMap<>();

        for (var i : items) {
            merged.merge(
                    i.productId(),
                    i,
                    (a, b) -> new CreateOrderRequest.OrderItemRequest(
                            a.productId(),
                            a.quantity() + b.quantity(),
                            maxDiscount(a.discount(), b.discount())
                    )
            );
        }

        return merged;
    }

    private static BigDecimal maxDiscount(BigDecimal a, BigDecimal b) {
        BigDecimal da = (a != null) ? a : BigDecimal.ZERO;
        BigDecimal db = (b != null) ? b : BigDecimal.ZERO;
        return da.max(db);
    }

    // READ OPERATIONS
    public Order findById(Long id) {
        if (id == null) {
            throw new DomainValidationException("orderId não pode ser nulo");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public List<Order> findByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new DomainValidationException("customerId não pode ser nulo");
        }

        return repository.findByCustomerId(customerId);
    }

    public List<Order> findByProductId(Long productId) {
        if (productId == null) {
            throw new DomainValidationException("productId não pode ser nulo");
        }
        return repository.findByProductId(productId);
    }

    public List<Order> findByPeriod(Instant start, Instant end) {
        if (start == null || end == null) {
            throw new DomainValidationException("Data inicial e final não podem ser nulas");
        }
        if (start.isAfter(end)) {
            throw new DomainValidationException("Data inicial não pode ser posterior à data final");
        }

        return repository.findByPeriod(start, end);
    }

    public BigDecimal findTotalAmountByCustomer(Long customerId) {
        if (customerId == null) {
            throw new DomainValidationException("customerId não pode ser nulo");
        }
        return repository.findTotalAmountByCustomer(customerId);
    }
}