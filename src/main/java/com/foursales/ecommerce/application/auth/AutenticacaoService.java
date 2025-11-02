package com.foursales.ecommerce.application.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.foursales.ecommerce.api.auth.dto.LoginRequest;
import com.foursales.ecommerce.api.auth.dto.LoginResponse;
import com.foursales.ecommerce.api.auth.dto.RegistrarUsuarioRequest;
import com.foursales.ecommerce.application.exceptions.CredenciaisInvalidasException;
import com.foursales.ecommerce.application.exceptions.EmailUsuarioDuplicadoException;
import com.foursales.ecommerce.domain.entities.PerfilTipo;
import com.foursales.ecommerce.domain.entities.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foursales.ecommerce.domain.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final TokenResponse tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        final String emailNorm = normalizeEmail(request.email());
        final String rawPassword = nonBlank(request.senha(), "Senha é obrigatória");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNorm, rawPassword)
            );
        } catch (BadCredentialsException e) {
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        } catch (DisabledException e) {
            throw new CredenciaisInvalidasException("Conta desativada");
        } catch (LockedException e) {
            throw new CredenciaisInvalidasException("Conta bloqueada");
        }

        UsuarioEntity usuario = usuarioRepository.findByEmail(emailNorm)
                .orElseThrow(() -> new CredenciaisInvalidasException("Usuário não encontrado"));

        if (!usuario.isAtivo()) {
            throw new CredenciaisInvalidasException("Conta desativada");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", usuario.getId().toString());
        claims.put("roles", List.of(usuario.getPerfil().name()));
        claims.put("name", usuario.getNome());

        String token = tokenService.gerarToken(usuario.getEmail(), claims);

        return new LoginResponse(token, "Bearer", System.currentTimeMillis());
    }

    @Transactional
    public void registrarUsuario(RegistrarUsuarioRequest request) {
        final String emailNorm = normalizeEmail(request.email());

        if (usuarioRepository.existsByEmail(emailNorm)) {
            throw new EmailUsuarioDuplicadoException("Email já cadastrado");
        }

        PerfilTipo perfil = request.perfil() != null ? request.perfil() : PerfilTipo.USER;

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nome(request.nome())
                .email(emailNorm)
                .senha(passwordEncoder.encode(request.senha()))
                .perfil(perfil)
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
    }

    private String normalizeEmail(String email) {
        if (email == null) throw new CredenciaisInvalidasException("Email é obrigatório");
        String norm = email.trim().toLowerCase(Locale.ROOT);
        if (norm.isEmpty()) throw new CredenciaisInvalidasException("Email é obrigatório");
        return norm;
    }

    private String nonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) throw new CredenciaisInvalidasException(message);
        return value;
    }
}
