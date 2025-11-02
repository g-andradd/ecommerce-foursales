package com.foursales.ecommerce.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email é obrigatorio")
        @Email(message = "Email invalido")
        String email,

        @NotBlank(message = "Senha é obrigatoria")
        String senha
) { }
