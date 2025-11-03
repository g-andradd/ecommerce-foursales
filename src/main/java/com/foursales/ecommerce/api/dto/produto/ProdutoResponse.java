package com.foursales.ecommerce.api.dto.produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProdutoResponse(
        UUID id,
        String nome,
        String descricao,
        BigDecimal preco,
        String categoria,
        Integer quantidadeEstoque,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
