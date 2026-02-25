package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

import static br.com.manoel.ordermanagement.model.MoneyConstants.*;

@ToString
public class Product {

    // ATTRIBUTES
    @Getter
    @EqualsAndHashCode.Include
    private Long id;

    @Getter
    private final String description;

    @Getter
    private final BigDecimal price;

    @Getter
    private int stockQuantity;

    @Getter
    private final Instant createdAt;

    // CONSTANTS
    private static final int DESCRIPTION_MAX_LENGTH = 200;

    // CONSTRUCTOR
    // Main constructor
    public Product(String description, BigDecimal price, int stockQuantity) {
        this(description, price, stockQuantity, null);
    }
    // Constructor for testing (createdAt injection)
    public Product(String description, BigDecimal price, int stockQuantity, Instant createdAt) {
        description = normalizeString(description);
        validateDescription(description);
        this.description = description;

        validatePrice(price);
        this.price = price.setScale(MONEY_SCALE, MONEY_ROUNDING);

        validateStockQuantity(stockQuantity);
        this.stockQuantity = stockQuantity;

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

    // description
    private void validateDescription(String description) {
        if (isNullOrBlank(description)) {
            throw new DomainValidationException("Descrição vazia ou inexistente");
        }

        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new DomainValidationException("Descrição excede " + DESCRIPTION_MAX_LENGTH + " caracteres");
        }
    }

    // price
    private void validatePrice(BigDecimal value) {
        if (value == null) {
            throw new DomainValidationException("Valor não pode ser nulo.");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("O valor deve ser maior que zero");
        }
    }

    // stockQuantity
    public void addStock(int amount) {
        if (amount <= 0) {
            throw new DomainValidationException("O valor a adicionar deve ser maior que zero");
        }
        this.stockQuantity += amount;
    }

    public void removeStock(int amount) {
        if (amount <= 0) {
            throw new DomainValidationException("O valor deve ser maior que zero");
        }
        if (amount > this.stockQuantity) {
            throw new DomainValidationException("O valor excede a quantidade em estoque");
        }
        this.stockQuantity -= amount;
    }

    public boolean isStockEmpty() {
        return this.stockQuantity == 0;
    }

    public boolean hasStock(int amount) {
        return this.stockQuantity >= amount;
    }

    private void validateStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new DomainValidationException("O estoque não pode ser negativo");
        }
    }

    // Constants Getters
    public static int getDescriptionMaxLength() {
        return DESCRIPTION_MAX_LENGTH;
    }

    // UTILS
    private boolean isNullOrBlank(String string) {
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
        if (!(obj instanceof Product other)) return false;

        if (this.id == null || other.id == null) return false;

        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : System.identityHashCode(this);
    }
}
