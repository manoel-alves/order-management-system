package br.com.manoel.ordermanagement.repository;

import br.com.manoel.ordermanagement.model.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByProductId(Long productId);

    BigDecimal findTotalAmountByCustomer(Long customerId);

    List<Order> findAll();

    List<Order> findByPeriod(Instant start, Instant end);

}