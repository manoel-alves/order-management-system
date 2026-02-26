package br.com.manoel.ordermanagement.model;

import br.com.manoel.ordermanagement.exception.domain.DomainValidationException;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

import static br.com.manoel.ordermanagement.model.MoneyConstants.*;

@ToString
public class OrderItem {

    @Getter
    private Long id;

    @Getter
    private final Long productId;

    @Getter
    private final int quantity;

    @Getter
    private final BigDecimal discount;

    @Getter
    private final BigDecimal unitPrice;

    @Getter
    private final BigDecimal totalPrice;

    public OrderItem(Long productId, int quantity,BigDecimal unitPrice , BigDecimal discount) {

        if (productId == null) {
            throw new DomainValidationException("Produto não pode ser nulo");
        }
        this.productId = productId;

        validateQuantity(quantity);
        this.quantity = quantity;

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("Preço unitário inválido");
        }
        this.unitPrice = unitPrice.setScale(MONEY_SCALE, MONEY_ROUNDING);

        BigDecimal effectiveDiscount = discount != null ? discount : BigDecimal.ZERO;
        effectiveDiscount = effectiveDiscount.setScale(MONEY_SCALE, MONEY_ROUNDING);

        validateDiscount(effectiveDiscount, getSubtotal(quantity));
        this.discount = effectiveDiscount;

        this.totalPrice =  getSubtotal(quantity).subtract(effectiveDiscount).setScale(MONEY_SCALE, MONEY_ROUNDING);
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID já definido. Não é permitido alterar.");
        }
        this.id = id;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("Quantidade deve ser maior que zero");
        }
    }

    private void  validateDiscount(BigDecimal discount, BigDecimal subtotal) {
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("Desconto inválido");
        }
        if(discount.compareTo(subtotal) > 0) {
            throw new DomainValidationException("Desconto não pode exceder o subtotal");
        }
    }

    private BigDecimal getSubtotal(int quantity) {
        return this.unitPrice.multiply(BigDecimal.valueOf(quantity))
                .setScale(MONEY_SCALE, MONEY_ROUNDING);
    }

    // EQUALS and HASHCODE
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OrderItem other)) return false;

        return this.productId.equals(other.getProductId());
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }
}
