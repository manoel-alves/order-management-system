package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateCustomerRequest;
import br.com.manoel.ordermanagement.dto.response.CustomerResponse;
import br.com.manoel.ordermanagement.model.Customer;
import br.com.manoel.ordermanagement.service.CustomerService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {

        Customer customer = service.create(request.name(), request.email());

        CustomerResponse response = toResponse(customer);

        return ResponseEntity.created(URI.create("/customers/" + customer.getId())).body(response);
    }

    //READ
    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        Customer customer = service.findById(id);

        return ResponseEntity.ok(toResponse(customer));
    }

    // Get by Name
    @GetMapping(params = "name")
    public ResponseEntity<List<CustomerResponse>> getByName(@RequestParam String name) {

        List<CustomerResponse> response = service.findByName(name)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // List all customers
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAll() {
        List<CustomerResponse> response = service.findAll().stream().map(this::toResponse).toList();

        return ResponseEntity.ok(response);
    }

    // UTILS
    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }
}
