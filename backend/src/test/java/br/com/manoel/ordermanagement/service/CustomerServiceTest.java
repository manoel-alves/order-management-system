package br.com.manoel.ordermanagement.service;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import br.com.manoel.ordermanagement.exception.domain.ResourceNotFoundException;
import br.com.manoel.ordermanagement.model.Customer;
import br.com.manoel.ordermanagement.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository repository;
    private CustomerService service;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);
        service = new CustomerService(repository);
    }

    // CREATION
    // Successful creation
    @Test
    void create_shouldReturnSavedCustomer() {
        Customer saved = new Customer("Valid Name", "valid@email.com");
        saved.setId(1L);

        when(repository.save(any(Customer.class))).thenReturn(saved);

        Customer result = service.create("Valid Name", "valid@email.com");

        assertEquals(1L, result.getId());
        assertEquals("Valid Name", result.getName());
        assertEquals("valid@email.com", result.getEmail());

        verify(repository, times(1)).save(any(Customer.class));
    }

    // with invalid name -> throw exception
    @Test
    void create_withInvalidName_shouldThrowDomainValidationException() {
        assertThrows(DomainValidationException.class, () ->
                service.create("123", "valid@email.com")
        );
    }

    // with invalid email -> throw exception
    @Test
    void create_withInvalidEmail_shouldThrowDomainValidationException() {
        assertThrows(DomainValidationException.class, () ->
                service.create("Valid Name", "invalid-email")
        );
    }

    // FIND BY ID
    // Existing id -> return Customer
    @Test
    void findById_existingId_shouldReturnCustomer() {
        Customer customer = new Customer("Valid Name", "valid@email.com");
        customer.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = service.findById(1L);
        assertEquals(customer, result);
    }

    // Non-existing id -> throw exception
    @Test
    void findById_nonExistingId_shouldThrow() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    // FIND BY NAME
    // returns matching list
    @Test
    void findByName_shouldReturnMatchingList() {
        Customer customer = new Customer("Valid Name", "valid@email.com");
        when(repository.findByName("Valid Name")).thenReturn(List.of(customer));

        List<Customer> result = service.findByName("Valid Name");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(customer, result.getFirst());
    }

    // returns all customers
    @Test
    void findAll_shouldReturnAllCustomers() {
        Customer customer1 = new Customer("Valid Name", "valid1@email.com");
        Customer customer2 = new Customer("Valid Name Two", "valid2@email.com");
        when(repository.findAll()).thenReturn(List.of(customer1, customer2));

        List<Customer> result = service.findAll();

        assertEquals(2, result.size());
    }

    // Returns empty list if no match
    @Test
    void findByName_noMatch_shouldReturnEmptyList() {
        when(repository.findByName("Unknown")).thenReturn(List.of());

        List<Customer> result = service.findByName("Unknown");

        assertTrue(result.isEmpty());
    }

    // Returns list with multiple customers
    @Test
    void findByName_multipleMatches_shouldReturnAll() {
        Customer customer1 = new Customer("Valid Name", "valid1@example.com");
        Customer customer2 = new Customer("Valid Name", "valid2@example.com");
        when(repository.findByName("Valid Name")).thenReturn(List.of(customer1, customer2));

        List<Customer> result = service.findByName("Valid Name");

        assertEquals(2, result.size());
        assertEquals(customer1, result.get(0));
        assertEquals(customer2, result.get(1));
    }
}