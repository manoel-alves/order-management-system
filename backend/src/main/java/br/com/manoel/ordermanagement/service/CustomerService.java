package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.dto.request.CreateCustomerRequest;
import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Customer;
import br.com.manoel.ordermanagement.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public Customer create(CreateCustomerRequest request) {
        Customer customer = new Customer(request.name(), request.email());
        return repository.save(customer);
    }

    // READ
    // Get ID
    public Customer findById(Long id) {
        if (id == null) {
            throw new DomainValidationException("customerId não pode ser nulo");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public List<Customer> findByName(String name) {
        if (name == null) {
            throw new DomainValidationException("name não pode ser nulo");
        }
        return repository.findByName(name);
    }

    // List all
    public List<Customer> findAll() {
        return repository.findAll();
    }
}