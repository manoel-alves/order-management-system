package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateProductRequest;
import br.com.manoel.ordermanagement.dto.response.ProductResponse;
import br.com.manoel.ordermanagement.mapper.ProductMapper;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {

        Product product = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(ProductMapper.toResponse(product));
    }

    // READ
    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ProductMapper.toResponse(service.findById(id))
        );
    }

    // Get by Description
    @GetMapping(params = "description")
    public ResponseEntity<List<ProductResponse>> getByDescription(@RequestParam String description) {
        return ResponseEntity.ok(toResponseList(service.findByDescription(description)));
    }

    // List all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(toResponseList(service.findAll()));
    }

    // INTERNAL HELPER
    private static List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream().map(ProductMapper::toResponse).toList();
    }
}