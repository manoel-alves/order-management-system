package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    // Test customer creation
    @Test
    void createCustomer_withValidData_shouldSucceed() {
        Customer customer = new Customer("Valid Name", "valid@email.com");

        assertNotNull(customer.getName());
        assertNotNull(customer.getEmail());
        assertNotNull(customer.getCreatedAt());
    }

    // Test name null or blank
    @Test
    void createCustomer_withEmptyName_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("", "valid@email.com")
        );

        assertEquals("Nome vazio ou inexistente", ex.getMessage());
    }
    
    // Test invalid name format
    @Test
    void createCustomer_withInvalidName_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("123", "valid@email.com")
        );

        assertEquals("Nome deve conter apenas letras e espaços únicos", ex.getMessage());
    }

    // Test double space between names in name
    @Test
    void createCustomer_withDoubleSpaces_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("Invalid  Name", "valid@email.com")
        );

        assertEquals("Nome não pode conter espaços consecutivos", ex.getMessage());
    }

    // Test name that exceeds max length
    @Test
    void createCustomer_nameExceedsMaxLength_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("a".repeat(151), "valid@email.com")
        );
        assertEquals("Nome excede " + Customer.getNameMaxLength() + " caracteres", ex.getMessage());
    }

    // Test email null or blank
    @Test
    void createCustomer_withEmptyEmail_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("Valid Name", "")
        );

        assertEquals("Email vazio ou inexistente", ex.getMessage());
    }

    // Test Email Validation
    @Test
    void createCustomer_withInvalidEmail_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("Valid Name", "invalid-email")
        );

        assertEquals("Email com formato inválido", ex.getMessage());
    }

    // Test email that exceeds max length
    @Test
    void createCustomer_emailExceedsMaxLength_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("Valid Name", "a".repeat(141) + "@email.com")
        );
        assertEquals("Email excede " + Customer.getEmailMaxLength() + " caracteres", ex.getMessage());
    }

    // Test reassign id
    @Test
    void setIdTwice_shouldThrow() {
        Customer customer = new Customer("Valid Name", "valid@email.com");
        customer.setId(1L);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> customer.setId(2L)
        );

        assertEquals("ID já definido. Não é permitido alterar.", ex.getMessage());
    }
}