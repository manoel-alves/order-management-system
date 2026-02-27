package br.com.manoel.ordermanagement.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        Instant orderDate
) {

    public record OrderItemResponse(
            Long productId,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal discount,
            BigDecimal totalPrice
    ) {}
}