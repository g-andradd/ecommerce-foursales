package com.foursales.ecommerce.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErroCampo", description = "Detalha um campo que falhou na validação da requisição.")
public record ErroCampo(
        @Schema(description = "Nome do campo inválido", example = "email")
        String campo,

        @Schema(description = "Descrição do motivo da falha", example = "Email é obrigatorio")
        String mensagem) {
}
