package com.foursales.ecommerce.api.erros;

import java.time.OffsetDateTime;
import java.util.List;

public record ErroResposta(String codigo, String mensagem, OffsetDateTime timestamp, List<ErroCampo> campos) {
}
