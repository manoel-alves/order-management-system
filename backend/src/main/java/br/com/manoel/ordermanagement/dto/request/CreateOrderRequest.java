package br.com.manoel.ordermanagement.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "Cliente não pode ser nulo")
        Long customerId,

        @NotNull(message = "Pedido precisa ter pelo menos 1 item")
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "Produto não pode ser nulo")
            Long productId,

            @Min(value = 1, message = "Quantidade deve ser maior que zero")
            int quantity,

            @DecimalMin(value = "0.00", message = "Desconto não pode ser negativo")
            BigDecimal discount
    ) {}
}