package com.foursales.ecommerce.api.exception;

import java.time.OffsetDateTime;
import java.util.List;

public record ErroResponse(String codigo, String mensagem, OffsetDateTime timestamp, List<ErroCampo> campos) {
}
