package com.foursales.ecommerce.domain.repository;

import com.foursales.ecommerce.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select u.id from Usuario u where u.email = :email")
    Optional<UUID> findIdByEmail(String email);
}
