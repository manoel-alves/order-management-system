package br.com.manoel.ordermanagement.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Descrição vazia ou inexistente")
        @Size(max = 200, message = "Descrição excede 200 caracteres")
        String description,

        @NotNull(message = "Valor não pode ser nulo.")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal price,

        @Min(value = 0, message = "O estoque não pode ser negativo")
        int stockQuantity
) {}