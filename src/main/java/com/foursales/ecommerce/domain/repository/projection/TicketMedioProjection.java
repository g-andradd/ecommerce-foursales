package com.foursales.ecommerce.domain.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface TicketMedioProjection {

    UUID getUsuarioId();

    BigDecimal getTicketMedio();
}
