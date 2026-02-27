package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.manoel.ordermanagement.model.MoneyConstants.*;

@ToString
public class Order {

    // ATTRIBUTES
    @Getter
    private Long id;

    @Getter
    private final Long customerId;

    @Getter
    private final Instant orderDate;

    @Getter
    private BigDecimal totalAmount;

    private final List<OrderItem> items = new ArrayList<>();

    // CONSTRUCTORS
    public Order(Long customerId) {
        this(customerId, Instant.now(), null);
    }

    public Order(Long customerId, Instant orderDate, List<OrderItem> items) {
        validateCustomer(customerId);
        this.customerId = customerId;

        this.orderDate = (orderDate != null) ? orderDate : Instant.now();

        if (items != null) {
            for (OrderItem item : items) {
                addItemInternal(item);
            }
        }

        recalculateTotal();
    }

    // METHODS
    // id
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID já definido. Não é permitido alterar.");
        }
        this.id = id;
    }

    // customer
    private void validateCustomer(Long customerId) {
        if (customerId == null) {
            throw new DomainValidationException("O ID de cliente não pode ser nulo");
        }
    }

    // items
    public void addItem(OrderItem item) {
        ensureNotPersisted();
        addItemInternal(item);
        recalculateTotal();
    }

    private void addItemInternal(OrderItem item) {
        if (item == null) throw new DomainValidationException("Item não pode ser nulo");

        Optional<OrderItem> existingOpt = findItemByProductId(item.getProductId());
        if (existingOpt.isPresent()) {
            OrderItem existing = existingOpt.get();

            if (existing.getUnitPrice().compareTo(item.getUnitPrice()) != 0) {
                throw new DomainValidationException(
                        "Preço unitário divergente para o mesmo produto"
                );
            }

            int newQuantity = existing.getQuantity() + item.getQuantity();
            BigDecimal newDiscount = existing.getDiscount().max(item.getDiscount());

            OrderItem merged = new OrderItem(
                    item.getProductId(),
                    newQuantity,
                    item.getUnitPrice(),
                    newDiscount
            );

            items.remove(existing);
            items.add(merged);
        } else {
            items.add(item);
        }
    }

    private Optional<OrderItem> findItemByProductId(Long productId) {
        return items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    // totalAmount
    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(MONEY_SCALE, MONEY_ROUNDING);
    }

    private void ensureNotPersisted() {
        if (this.id != null) {
            throw new DomainValidationException("Pedido já persistido");
        }
    }

    // EQUALS and HASHCODE
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Order other)) return false;

        if (this.id == null || other.getId() == null) return false;

        return this.id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : System.identityHashCode(this);
    }
}