package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateOrderRequest;
import br.com.manoel.ordermanagement.dto.response.OrderResponse;
import br.com.manoel.ordermanagement.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;

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
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response = service.create(request);

        return ResponseEntity
                .created(URI.create("/orders/" + response.id()))
                .body(response);
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findResponseById(id));
    }

    @GetMapping(params = "customerId")
    public ResponseEntity<List<OrderResponse>> getByCustomer(@RequestParam Long customerId) {
        return ResponseEntity.ok(service.findByCustomerIdResponse(customerId));
    }

    @GetMapping(params = "productId")
    public ResponseEntity<List<OrderResponse>> getByProduct(@RequestParam Long productId) {
        return ResponseEntity.ok(service.findByProductIdResponse(productId));
    }

    @GetMapping("/by-period")
    public ResponseEntity<List<OrderResponse>> getByPeriod(@RequestParam("start") Instant start,
                                                           @RequestParam("end") Instant end) {
        return ResponseEntity.ok(service.findByPeriodResponse(start, end));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(service.findAllResponses());
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalAmountByCustomer(@RequestParam Long customerId) {
        return ResponseEntity.ok(service.findTotalAmountByCustomer(customerId));
    }

}