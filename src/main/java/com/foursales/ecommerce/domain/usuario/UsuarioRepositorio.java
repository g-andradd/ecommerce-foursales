package com.foursales.ecommerce.domain.usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositorio {

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorId(UUID id);

    Usuario salvar(Usuario usuario);
}
