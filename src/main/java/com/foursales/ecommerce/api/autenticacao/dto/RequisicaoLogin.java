package com.foursales.ecommerce.api.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequisicaoLogin(
        @NotBlank(message = "Email eh obrigatorio")
        @Email(message = "Email invalido")
        String email,

        @NotBlank(message = "Senha eh obrigatoria")
        String senha) {
}
