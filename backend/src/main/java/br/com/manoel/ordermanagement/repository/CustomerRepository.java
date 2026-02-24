package br.com.manoel.ordermanagement.repository;

import br.com.manoel.ordermanagement.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    List<Customer> findByName(String name);

    List<Customer> findAll();

}
