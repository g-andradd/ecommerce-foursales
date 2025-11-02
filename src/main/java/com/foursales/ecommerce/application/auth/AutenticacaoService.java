package com.foursales.ecommerce.application.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.foursales.ecommerce.api.auth.dto.LoginRequest;
import com.foursales.ecommerce.api.auth.dto.LoginResponse;
import com.foursales.ecommerce.api.auth.dto.RegistrarUsuarioRequest;
import com.foursales.ecommerce.application.auth.exception.CredenciaisInvalidasException;
import com.foursales.ecommerce.application.auth.exception.EmailUsuarioDuplicadoException;
import com.foursales.ecommerce.domain.entity.TipoPerfil;
import com.foursales.ecommerce.domain.entity.Usuario;
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
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        final String emailNormalizado = normalizeEmail(request.email());
        final String senhaNormalizada = normalizeSenha(request.senha());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNormalizado, senhaNormalizada)
            );
        } catch (BadCredentialsException e) {
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        } catch (DisabledException e) {
            throw new CredenciaisInvalidasException("Conta desativada");
        } catch (LockedException e) {
            throw new CredenciaisInvalidasException("Conta bloqueada");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new CredenciaisInvalidasException("Usuário não encontrado"));

        if (!usuario.isAtivo()) {
            throw new CredenciaisInvalidasException("Conta desativada");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", usuario.getId().toString());
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

        TipoPerfil perfil = request.perfil() != null ? request.perfil() : TipoPerfil.USER;

        Usuario usuario = Usuario.builder()
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

    private String normalizeSenha(String value) {
        if (value == null || value.trim().isEmpty()) throw new CredenciaisInvalidasException("Senha é obrigatória");
        return value;
    }
}
