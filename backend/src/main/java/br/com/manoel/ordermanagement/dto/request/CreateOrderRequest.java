package br.com.manoel.ordermanagement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(

        @NotNull(message = "ID de cliente não pode ser nulo")
        Long customerId,

        @NotEmpty(message = "Pedido precisa ter pelo menos 1 item")
        List<@Valid OrderItemRequest> items
) {
    public record OrderItemRequest(

            @NotNull(message = "Produto não pode ser nulo")
            Long productId,

            @Positive(message = "Quantidade deve ser maior que zero")
            int quantity,

            @PositiveOrZero(message = "Desconto não pode ser negativo")
            @Digits(integer = 12, fraction = 2, message = "Desconto deve ter no máximo 2 casas decimais")
            BigDecimal discount
    ) {}
}