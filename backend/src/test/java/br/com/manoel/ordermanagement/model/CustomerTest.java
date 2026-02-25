package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    // Test Instant injection
    @Test
    void createCustomer_withCustomCreatedAt_shouldRespectValue() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");

        Customer customer = new Customer("Valid Name", "valid@email.com", now);

        assertEquals(now, customer.getCreatedAt());
    }

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

        assertEquals("Nome deve conter apenas letras e espaços", ex.getMessage());
    }

    // Test name normalization
    @Test
    void createCustomer_nameWithExtraSpaces_shouldNormalize() {
        Customer customer = new Customer(" Valid  Name ", "valid@email.com");

        assertEquals("Valid Name", customer.getName());
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

    // Test email normalization
    @Test
    void createCustomer_emailWithExtraSpaces_shouldNormalize() {
        Customer customer = new Customer("Valid Name", " valid@email.com ");

        assertEquals("valid@email.com", customer.getEmail());
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