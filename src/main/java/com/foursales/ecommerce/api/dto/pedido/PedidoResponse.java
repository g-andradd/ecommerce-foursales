package com.foursales.ecommerce.api.dto.pedido;

import com.foursales.ecommerce.domain.entity.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        StatusPedido status,
        BigDecimal total,
        LocalDateTime criadoEm,
        LocalDateTime pagoEm,
        List<ItemResponse> itens
) {
    public record ItemResponse(UUID produtoId, Integer quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {}
}
