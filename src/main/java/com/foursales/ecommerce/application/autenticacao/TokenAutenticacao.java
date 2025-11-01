package com.foursales.ecommerce.application.autenticacao;

import java.time.OffsetDateTime;

public record TokenAutenticacao(String token, String tipo, OffsetDateTime expiraEm) {
}
