package com.foursales.ecommerce.application.autenticacao;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foursales.ecommerce.domain.usuario.UsuarioRepositorio;

@Service
public class AutenticarUsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final GerenciadorTokenAutenticacao gerenciadorTokenAutenticacao;

    public AutenticarUsuarioService(
            UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder,
            GerenciadorTokenAutenticacao gerenciadorTokenAutenticacao) {
        this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
        this.gerenciadorTokenAutenticacao = Objects.requireNonNull(gerenciadorTokenAutenticacao);
    }

    public TokenAutenticacao autenticar(String email, String senha) {
        var usuario = usuarioRepositorio.buscarPorEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return gerenciadorTokenAutenticacao.gerar(usuario);
    }
}
