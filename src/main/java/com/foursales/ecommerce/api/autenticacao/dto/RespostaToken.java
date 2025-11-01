package com.foursales.ecommerce.api.autenticacao.dto;

import java.time.OffsetDateTime;

public record RespostaToken(String token, String tipo, OffsetDateTime expiraEm) {
}
