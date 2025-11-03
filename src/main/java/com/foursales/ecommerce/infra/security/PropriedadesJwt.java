package com.foursales.ecommerce.infra.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record PropriedadesJwt(String jwtSecret, long jwtExpirationSeconds) {

    public Duration expiracao() {
        return Duration.ofSeconds(jwtExpirationSeconds);
    }
}
