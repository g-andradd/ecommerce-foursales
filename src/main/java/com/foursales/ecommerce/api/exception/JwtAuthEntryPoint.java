package com.foursales.ecommerce.api.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(jakarta.servlet.http.HttpServletRequest req,
                         jakarta.servlet.http.HttpServletResponse res,
                         org.springframework.security.core.AuthenticationException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        String body = """
        {"codigo":"NAO_AUTORIZADO","mensagem":"Credenciais ausentes ou invalidas","timestamp":"%s","campos":[]}
        """.formatted(java.time.OffsetDateTime.now().toString());
        res.getWriter().write(body);
    }
}

