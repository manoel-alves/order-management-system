package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import lombok.*;

import java.time.Instant;

@ToString
public class Customer {

    // ATTRIBUTES
    @Getter
    private Long id;

    @Getter
    private final String name;

    @Getter
    private final String email;

    @Getter
    private final Instant createdAt;

    // CONSTANTS
    private static final int NAME_MAX_LENGTH = 150;
    private static final int EMAIL_MAX_LENGTH = 150;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // CONSTRUCTOR
    // Main Constructor
    public Customer(String name, String email) {
        this(name, email, null);
    }

    // Constructor for testing
    public Customer(String name, String email,  Instant createdAt) {
        validateName(name);
        this.name = name.trim();

        validateEmail(email);
        this.email = email.trim();

        this.createdAt = (createdAt != null) ? createdAt : Instant.now();
    }

    // METHODS
    // id
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID já definido. Não é permitido alterar.");
        }
        this.id = id;
    }

    // name
    private void validateName(String name) {
        if (isNullOrBlank(name)) {
            throw new DomainValidationException("Nome vazio ou inexistente");
        }
        if (name.trim().length() > NAME_MAX_LENGTH) {
            throw new DomainValidationException("Nome excede " + NAME_MAX_LENGTH + " caracteres");
        }
    }

    // email
    private void validateEmail(String email) {
        if (isNullOrBlank(email)) {
            throw new DomainValidationException("Email vazio ou inexistente");
        }

        String normalizedEmail = email.trim();
        if (normalizedEmail.length() > EMAIL_MAX_LENGTH) {
            throw new DomainValidationException("Email excede " + EMAIL_MAX_LENGTH + " caracteres");
        }
        if (!normalizedEmail.matches(EMAIL_REGEX)) {
            throw new DomainValidationException("Email com formato inválido");
        }
    }

    // UTILS
    private static boolean isNullOrBlank( String string ) {
        return (string == null || string.isBlank());
    }

    // EQUALS and HASHCODE
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Customer other)) return false;

        if (this.id == null || other.id == null) return false;

        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : System.identityHashCode(this);
    }
}
