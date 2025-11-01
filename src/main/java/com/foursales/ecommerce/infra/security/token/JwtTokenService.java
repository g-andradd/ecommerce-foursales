package com.foursales.ecommerce.infra.security.token;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.foursales.ecommerce.application.autenticacao.GerenciadorTokenAutenticacao;
import com.foursales.ecommerce.application.autenticacao.TokenAutenticacao;
import com.foursales.ecommerce.domain.usuario.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenService implements GerenciadorTokenAutenticacao {

    private static final String TIPO_TOKEN = "Bearer";

    private final PropriedadesJwt propriedadesJwt;
    private final SecretKey chaveAssinatura;

    public JwtTokenService(PropriedadesJwt propriedadesJwt) {
        this.propriedadesJwt = propriedadesJwt;
        this.chaveAssinatura = Keys.hmacShaKeyFor(Decoders.BASE64.decode(propriedadesJwt.jwtSecret()));
    }

    @Override
    public TokenAutenticacao gerar(Usuario usuario) {
        var agora = OffsetDateTime.now(ZoneOffset.UTC);
        var expiraEm = agora.plus(propriedadesJwt.expiracao());

        var jwt = Jwts.builder()
                .subject(usuario.getId().toString())
                .issuedAt(Date.from(agora.toInstant()))
                .expiration(Date.from(expiraEm.toInstant()))
                .signWith(chaveAssinatura)
                .compact();

        return new TokenAutenticacao(jwt, TIPO_TOKEN, expiraEm);
    }

    @Override
    public Optional<UUID> recuperarUsuarioId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chaveAssinatura)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(UUID.fromString(claims.getSubject()));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
