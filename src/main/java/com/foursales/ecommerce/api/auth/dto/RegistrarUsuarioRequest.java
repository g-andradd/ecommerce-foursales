package com.foursales.ecommerce.api.auth.dto;

import com.foursales.ecommerce.domain.entities.PerfilTipo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarUsuarioRequest(
        @NotBlank(message = "Nome é obrigatorio")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
        String nome,

        @NotBlank(message = "Email é obrigatorio")
        @Size(max = 160, message = "Nome deve ter no maximo 160 caracteres")
        @Email(message = "Email invalido")
        String email,

        @NotBlank(message = "Senha é obrigatoria")
        @Size(min = 3, max = 60, message = "Senha deve ter entre 3 e 60 caracteres")
        String senha,

        @NotNull(message = "Perfil é obrigatório")
        PerfilTipo perfil
) { }
