package com.foursales.ecommerce.application.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenResponse {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expiration;

    private SecretKey signKey;

    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarToken(String username, Map<String, Object> claims) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(agora)
                .expiration(expira)
                .signWith(signKey)
                .compact();
    }

    public String validarETrazerUsuario(String token) {
        try {
            Claims body = Jwts.parser()
                    .verifyWith(signKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return body.getSubject();
        } catch (SignatureException e) {
            //todo Melhorar retorno de erro
            System.out.println("Assinatura inválida: " + e.getMessage());
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        try {
            Claims body = Jwts.parser()
                    .verifyWith(signKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = body.getSubject();
            Date exp = body.getExpiration();

            return username.equals(userDetails.getUsername()) && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
