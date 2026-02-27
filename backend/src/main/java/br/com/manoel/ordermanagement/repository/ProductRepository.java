package br.com.manoel.ordermanagement.repository;

import br.com.manoel.ordermanagement.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findByDescription(String description);

    List<Product> findAll();

    boolean decrementStock(Long productId, int amount);
}