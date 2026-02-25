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

    // Constructor for testing (createdAt injection)
    public Customer(String name, String email,  Instant createdAt) {
        name = normalizeString(name);
        validateName(name);
        this.name = name;

        email = normalizeString(email);
        validateEmail(email);
        this.email = email;

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

        if (name.length() > NAME_MAX_LENGTH) {
            throw new DomainValidationException("Nome excede " + NAME_MAX_LENGTH + " caracteres");
        }
        if (!name.matches("^[\\p{L} ]+$")) {
            throw new DomainValidationException("Nome deve conter apenas letras e espaços");
        }
    }

    // email
    private void validateEmail(String email) {
        if (isNullOrBlank(email)) {
            throw new DomainValidationException("Email vazio ou inexistente");
        }

        if (email.length() > EMAIL_MAX_LENGTH) {
            throw new DomainValidationException("Email excede " + EMAIL_MAX_LENGTH + " caracteres");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new DomainValidationException("Email com formato inválido");
        }
    }

    // Constants Getters
    public static int getNameMaxLength() {
        return NAME_MAX_LENGTH;
    }

    public static int getEmailMaxLength() {
        return EMAIL_MAX_LENGTH;
    }

    // UTILS
    private static boolean isNullOrBlank( String string ) {
        return (string == null || string.isBlank());
    }

    private String normalizeString(String string) {
        if (isNullOrBlank(string)) return string;
        return string.trim().replaceAll(" {2,}", " ");
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
