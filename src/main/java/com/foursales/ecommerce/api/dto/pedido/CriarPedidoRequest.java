package com.foursales.ecommerce.api.dto.pedido;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record CriarPedidoRequest(
        @NotEmpty(message = "O pedido deve conter ao menos um item")
        List<ItemPedidoRequest> itens
) {
    public record ItemPedidoRequest(@NotNull UUID produtoId, @NotNull @Positive Integer quantidade) {}
}
