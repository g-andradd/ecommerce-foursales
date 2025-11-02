package com.foursales.ecommerce.api.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAcessoNegadoHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(jakarta.servlet.http.HttpServletRequest req,
                       jakarta.servlet.http.HttpServletResponse res,
                       org.springframework.security.access.AccessDeniedException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/json");
        String body = """
        {"codigo":"ACESSO_NEGADO","mensagem":"Permissoes insuficientes","timestamp":"%s","campos":[]}
        """.formatted(java.time.OffsetDateTime.now().toString());
        res.getWriter().write(body);
    }
}
