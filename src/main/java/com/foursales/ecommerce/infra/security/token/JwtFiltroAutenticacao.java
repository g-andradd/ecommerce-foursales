package com.foursales.ecommerce.infra.security.token;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foursales.ecommerce.application.autenticacao.GerenciadorTokenAutenticacao;
import com.foursales.ecommerce.domain.usuario.UsuarioRepositorio;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFiltroAutenticacao extends OncePerRequestFilter {

    private final GerenciadorTokenAutenticacao gerenciadorTokenAutenticacao;
    private final UsuarioRepositorio usuarioRepositorio;

    public JwtFiltroAutenticacao(GerenciadorTokenAutenticacao gerenciadorTokenAutenticacao,
            UsuarioRepositorio usuarioRepositorio) {
        this.gerenciadorTokenAutenticacao = gerenciadorTokenAutenticacao;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var contexto = SecurityContextHolder.getContext();
        if (contexto.getAuthentication() == null) {
            extrairToken(request)
                    .flatMap(gerenciadorTokenAutenticacao::recuperarUsuarioId)
                    .flatMap(usuarioRepositorio::buscarPorId)
                    .ifPresent(usuario -> {
                        var autoridades = usuario.autoridades().stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();
                        var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, autoridades);
                        autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        contexto.setAuthentication(autenticacao);
                    });
        }

        filterChain.doFilter(request, response);
    }

    private java.util.Optional<String> extrairToken(HttpServletRequest request) {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isBlank()) {
            return java.util.Optional.empty();
        }

        if (header.startsWith("Bearer ")) {
            return java.util.Optional.of(header.substring(7));
        }

        return java.util.Optional.empty();
    }
}
