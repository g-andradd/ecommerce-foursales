package com.foursales.ecommerce.application.usuario;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foursales.ecommerce.domain.usuario.PapelUsuario;
import com.foursales.ecommerce.domain.usuario.Usuario;
import com.foursales.ecommerce.domain.usuario.UsuarioRepositorio;

@Service
public class CadastroUsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public CadastroUsuarioService(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Transactional
    public Usuario cadastrar(ComandoCadastroUsuario comando) {
        var papel = comando.papel() != null ? comando.papel() : PapelUsuario.USER;
        var email = comando.email();

        usuarioRepositorio.buscarPorEmail(email).ifPresent(usuario -> {
            throw new EmailUsuarioDuplicadoException(email);
        });

        var senhaHash = passwordEncoder.encode(comando.senha());
        var usuario = Usuario.novo(comando.nome(), email, senhaHash, papel);
        return usuarioRepositorio.salvar(usuario);
    }
}
