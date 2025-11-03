package com.foursales.ecommerce.domain.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface TopCompradorProjection {

    UUID getUsuarioId();

    BigDecimal getTotalGasto();
}
