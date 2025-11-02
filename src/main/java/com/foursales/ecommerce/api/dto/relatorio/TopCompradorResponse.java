package com.foursales.ecommerce.api.dto.relatorio;

import java.math.BigDecimal;
import java.util.UUID;

public record TopCompradorResponse(UUID usuarioId, BigDecimal totalGasto) {}
