package com.foursales.ecommerce.api.autenticacao.dto;

import com.foursales.ecommerce.domain.usuario.PapelUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequisicaoCadastroUsuario(
        @NotBlank(message = "Nome eh obrigatorio")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
        String nome,

        @NotBlank(message = "Email eh obrigatorio")
        @Email(message = "Email invalido")
        String email,

        @NotBlank(message = "Senha eh obrigatoria")
        @Size(min = 3, max = 60, message = "Senha deve ter entre 3 e 60 caracteres")
        String senha,

        PapelUsuario papel) {
}
