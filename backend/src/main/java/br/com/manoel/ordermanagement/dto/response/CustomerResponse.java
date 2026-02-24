package br.com.manoel.ordermanagement.dto.response;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        Instant createdAt
) {}
