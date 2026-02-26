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
    private final Customer customer;

    @Getter
    private final Instant orderDate;

    @Getter
    private BigDecimal totalAmount;

    private final List<OrderItem> items = new ArrayList<>();

    @Getter
    private boolean confirmed = false;

    // CONSTRUCTOR
    // Main constructor
    public Order(Customer customer) {
        this(customer, null);
    }

    // Constructor for testing
    public Order(Customer customer, Instant orderDate) {
        validateCustomer(customer);
        this.customer = customer;

        this.orderDate = (orderDate != null) ? orderDate : Instant.now();

        this.totalAmount = BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING);
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
    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new DomainValidationException("Cliente não pode ser nulo");
        }
    }

    // items
    public void addItem(Product product, int quantity) {
        addItem(product, quantity, BigDecimal.ZERO);
    }
    public void addItem(Product product, int quantity, BigDecimal discount) {

        ensureNotConfirmed();
        validateAddItemInput(product, quantity);

        BigDecimal effectiveDiscount = discount != null ? discount : BigDecimal.ZERO;

        Optional<OrderItem> existingOpt = findItemByProduct(product);

        OrderItem newItem;

        if (existingOpt.isPresent()) {
            OrderItem existing = existingOpt.get();

            int newQuantity = existing.getQuantity() + quantity;
            BigDecimal newDiscount = existing.getDiscount().max(effectiveDiscount);

            newItem = new OrderItem(product, newQuantity, newDiscount);

            items.remove(existing); // remove apenas o que existe
        } else {
            newItem = new OrderItem(product, quantity, effectiveDiscount);
        }

        if (!product.hasStock(newItem.getQuantity())) {
            throw new DomainValidationException(
                    "Estoque insuficiente para o produto: " + product.getDescription()
            );
        }

        items.add(newItem);
        recalculateTotal();
    }

    private void validateAddItemInput(Product product, int quantity) {
        if (product == null) {
            throw new DomainValidationException("Produto não pode ser nulo");
        }

        if (quantity <= 0) {
            throw new DomainValidationException("Quantidade deve ser maior que zero");
        }
    }

    private Optional<OrderItem> findItemByProduct(Product product) {
        return items.stream()
                .filter(item -> item.getProduct().equals(product))
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

    // Products stock update
    public void confirmOrder() {
        ensureNotConfirmed();

        if (items.isEmpty()) {
            throw new DomainValidationException("Pedido não possui itens");
        }

        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.removeStock(item.getQuantity());
        }
        this.confirmed = true;
    }

    private void ensureNotConfirmed() {
        if (confirmed) {
            throw new DomainValidationException("Pedido já confirmado");
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