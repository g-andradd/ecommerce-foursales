package com.foursales.ecommerce.application.autenticacao;

import java.util.Optional;
import java.util.UUID;

import com.foursales.ecommerce.domain.usuario.Usuario;

public interface GerenciadorTokenAutenticacao {

    TokenAutenticacao gerar(Usuario usuario);

    Optional<UUID> recuperarUsuarioId(String token);
}
