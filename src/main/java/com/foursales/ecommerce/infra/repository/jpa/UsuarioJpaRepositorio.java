package com.foursales.ecommerce.infra.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foursales.ecommerce.domain.usuario.Usuario;
import com.foursales.ecommerce.domain.usuario.UsuarioRepositorio;

@Repository
public interface UsuarioJpaRepositorio extends JpaRepository<Usuario, UUID>, UsuarioRepositorio {

    Optional<Usuario> findByEmail(String email);

    @Override
    default Optional<Usuario> buscarPorEmail(String email) {
        return findByEmail(email);
    }

    @Override
    default Optional<Usuario> buscarPorId(UUID id) {
        return findById(id);
    }

    @Override
    default Usuario salvar(Usuario usuario) {
        return save(usuario);
    }
}
