package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    // CREATE Customer

    @Test
    @DisplayName("Create Customer with valid data should succeed")
    void createCustomer_withValidData_shouldSucceed() {
        Customer customer = new Customer("Valid Name", "valid@email.com");

        assertEquals("Valid Name", customer.getName());
        assertEquals("valid@email.com", customer.getEmail());
        assertNotNull(customer.getCreatedAt());
    }

    @Test
    @DisplayName("Create Customer with custom createdAt should respect value")
    void createCustomer_withCustomCreatedAt_shouldRespectValue() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        Customer customer = new Customer("Valid Name", "valid@email.com", now);

        assertEquals(now, customer.getCreatedAt());
    }

    // NAME

    @Test
    @DisplayName("Create Customer with empty name should throw")
    void createCustomer_withEmptyName_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("", "valid@email.com")
        );
        assertEquals("Nome vazio ou inexistente", ex.getMessage());
    }

    @Test
    @DisplayName("Create Customer with invalid name should throw")
    void createCustomer_withInvalidName_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("123", "valid@email.com")
        );
        assertEquals("Nome deve conter apenas letras e espaços", ex.getMessage());
    }

    @Test
    @DisplayName("Create Customer with extra spaces in name should normalize")
    void createCustomer_nameWithExtraSpaces_shouldNormalize() {
        Customer customer = new Customer(" Valid  Name ", "valid@email.com");
        assertEquals("Valid Name", customer.getName());
    }

    @Test
    @DisplayName("Create Customer with name exceeding max length should throw")
    void createCustomer_nameExceedsMaxLength_shouldThrow() {
        String longName = "a".repeat(151);
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer(longName, "valid@email.com")
        );
        assertEquals("Nome excede " + Customer.getNameMaxLength() + " caracteres", ex.getMessage());
    }

    // EMAIL

    @Test
    @DisplayName("Create Customer with empty email should throw")
    void createCustomer_withEmptyEmail_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("Valid Name", "")
        );
        assertEquals("Email vazio ou inexistente", ex.getMessage());
    }

    @Test
    @DisplayName("Create Customer with extra spaces in email should normalize")
    void createCustomer_emailWithExtraSpaces_shouldNormalize() {
        Customer customer = new Customer("Valid Name", " valid@email.com ");
        assertEquals("valid@email.com", customer.getEmail());
    }

    @Test
    @DisplayName("Create Customer with invalid email should throw")
    void createCustomer_withInvalidEmail_shouldThrow() {
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("Valid Name", "invalid-email")
        );
        assertEquals("Email com formato inválido", ex.getMessage());
    }

    @Test
    @DisplayName("Create Customer with email exceeding max length should throw")
    void createCustomer_emailExceedsMaxLength_shouldThrow() {
        String longEmail = "a".repeat(141) + "@email.com";
        DomainValidationException ex = assertThrows(
                DomainValidationException.class,
                () -> new Customer("Valid Name", longEmail)
        );
        assertEquals("Email excede " + Customer.getEmailMaxLength() + " caracteres", ex.getMessage());
    }

    // ID

    @Test
    @DisplayName("Set ID twice should throw")
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