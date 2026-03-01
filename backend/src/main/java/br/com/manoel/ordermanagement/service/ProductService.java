package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.dto.request.CreateProductRequest;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Product;
import br.com.manoel.ordermanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public Product create(CreateProductRequest request) {
        Product product = new Product(request.description(), request.price(), request.stockQuantity());
        return repository.save(product);
    }

    // READ
    // Get by ID
    public Product findById(Long id) {
        if (id == null) {
            throw new DomainValidationException("productId não pode ser nulo");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    // Get by description
    public List<Product> findByDescription(String description) {
        if (description == null) {
            throw new DomainValidationException("description não pode ser nulo");
        }
        return repository.findByDescription(description);
    }

    // List all products
    public List<Product> findAll() {
        return repository.findAll();
    }

    public void decrementStock(Long productId, int amount) {
        if (productId == null) {
            throw new DomainValidationException("productId não pode ser nulo");
        }
        if (amount <= 0) {
            throw new DomainValidationException("amount deve ser maior que zero");
        }

        boolean updated = repository.decrementStock(productId, amount);
        if (!updated) {
            throw new DomainValidationException("Estoque insuficiente para o produto: " + "#" + productId);
        }
    }
}