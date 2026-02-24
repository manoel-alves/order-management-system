package br.com.manoel.ordermanagement.exception.domain;

public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
