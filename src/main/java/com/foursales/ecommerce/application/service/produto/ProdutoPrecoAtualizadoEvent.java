package com.foursales.ecommerce.application.service.produto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoPrecoAtualizadoEvent(UUID produtoId, BigDecimal novoPreco) {
}
