package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateCustomerRequest;
import br.com.manoel.ordermanagement.dto.response.CustomerResponse;
import br.com.manoel.ordermanagement.mapper.CustomerMapper;
import br.com.manoel.ordermanagement.model.Customer;
import br.com.manoel.ordermanagement.service.CustomerService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

        Customer customer = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(CustomerMapper.toResponse(customer));
    }

    //READ
    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                CustomerMapper.toResponse(service.findById(id))
        );
    }

    // Get by Name
    @GetMapping(params = "name")
    public ResponseEntity<List<CustomerResponse>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(toResponseList(service.findByName(name)));

    }

    // List all customers
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAll() {
        return ResponseEntity.ok(toResponseList(service.findAll()));
    }

    // INTERNAL HELPER
    private static List<CustomerResponse> toResponseList(List<Customer> customers) {
        return customers.stream().map(CustomerMapper::toResponse).toList();
    }
}
