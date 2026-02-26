package br.com.manoel.ordermanagement.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String description,
        BigDecimal price,
        int stockQuantity,
        Instant createdAt
) {}