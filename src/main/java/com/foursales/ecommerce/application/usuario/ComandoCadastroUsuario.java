package com.foursales.ecommerce.application.usuario;

import com.foursales.ecommerce.domain.usuario.PapelUsuario;

public record ComandoCadastroUsuario(
        String nome,
        String email,
        String senha,
        PapelUsuario papel) {
}
