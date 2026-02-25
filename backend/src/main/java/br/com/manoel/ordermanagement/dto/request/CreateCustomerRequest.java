package br.com.manoel.ordermanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank(message = "Nome vazio ou inexistente")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Email vazio ou inexistente")
        @Size(max = 150)
        @Email(message = "Email com formato inválido")
        String email
) {}
