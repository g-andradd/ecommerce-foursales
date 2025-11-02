package com.foursales.ecommerce.api.dto.produto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CriarProdutoRequest(
        @NotBlank(message = "Nome é obrigatorio")
        String nome,

        @Size(max=500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao,

        @NotNull(message = "Preço é obrigatorio")
        @PositiveOrZero(message = "Preço não pode ser negativo")
        BigDecimal preco,

        @Size(max = 80, message = "Categoria deve ter no máximo 80 caracteres")
        String categoria,

        @NotNull(message = "Quantidade em estoque é obrigatoria")
        @PositiveOrZero (message = "Quantidade em estoque não pode ser negativa")
        Integer quantidadeEstoque
) {}
