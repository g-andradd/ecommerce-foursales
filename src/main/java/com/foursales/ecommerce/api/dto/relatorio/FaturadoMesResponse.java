package com.foursales.ecommerce.api.dto.relatorio;

import java.math.BigDecimal;

public record FaturadoMesResponse(int ano, int mes, BigDecimal totalFaturado) {}
