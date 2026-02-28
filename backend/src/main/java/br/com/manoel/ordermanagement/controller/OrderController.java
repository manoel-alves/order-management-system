package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateOrderRequest;
import br.com.manoel.ordermanagement.dto.response.OrderResponse;
import br.com.manoel.ordermanagement.mapper.OrderMapper;
import br.com.manoel.ordermanagement.model.Order;
import br.com.manoel.ordermanagement.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request
    ) {

        Order order = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(OrderMapper.toResponse(order));
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                OrderMapper.toResponse(service.findById(id))
        );
    }

    @GetMapping(params = "customerId")
    public ResponseEntity<List<OrderResponse>> getByCustomer(@RequestParam Long customerId) {
        return ResponseEntity.ok(toResponseList(service.findByCustomerId(customerId)));
    }

    @GetMapping(params = "productId")
    public ResponseEntity<List<OrderResponse>> getByProduct(@RequestParam Long productId) {
        return ResponseEntity.ok(toResponseList(service.findByProductId(productId)));
    }

    @GetMapping("/by-period")
    public ResponseEntity<List<OrderResponse>> getByPeriod(
            @RequestParam("start") @DateTimeFormat(iso = ISO.DATE_TIME) Instant start,
            @RequestParam("end") @DateTimeFormat(iso = ISO.DATE_TIME) Instant end)
    {
        return ResponseEntity.ok(toResponseList(service.findByPeriod(start, end)));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(toResponseList(service.findAll()));
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalAmountByCustomer(@RequestParam Long customerId) {
        return ResponseEntity.ok(service.findTotalAmountByCustomer(customerId));
    }

    // INTERNAL HELPER
    private static List<OrderResponse> toResponseList(List<Order> orders) {
        return orders.stream().map(OrderMapper::toResponse).toList();
    }
}