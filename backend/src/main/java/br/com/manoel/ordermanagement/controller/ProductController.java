package br.com.manoel.ordermanagement.controller;

import br.com.manoel.ordermanagement.dto.request.CreateProductRequest;
import br.com.manoel.ordermanagement.dto.response.ProductResponse;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        Product product = service.create(
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        ProductResponse response = toResponse(product);

        return ResponseEntity.created(URI.create("/products/" + product.getId())).body(response);
    }

    // READ
    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        Product product = service.findById(id);

        return ResponseEntity.ok(toResponse(product));
    }

    // Get by Description (partial match)
    @GetMapping(params = "description")
    public ResponseEntity<List<ProductResponse>> getByDescription(@RequestParam String description) {

        List<ProductResponse> response = service.findByDescription(description)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // List all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<ProductResponse> response = service.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // UTILS
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt()
        );
    }
}