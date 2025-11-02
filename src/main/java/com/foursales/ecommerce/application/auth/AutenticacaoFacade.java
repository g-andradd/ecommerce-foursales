package com.foursales.ecommerce.application.auth;

import com.foursales.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AutenticacaoFacade {
    private final UsuarioRepository usuarioRepository;

    public UUID getUsuarioId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        String email = auth.getName();
        return usuarioRepository.findIdByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Usuario nao encontrado"));
    }

    public String getEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return auth.getName();
    }

    public boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
