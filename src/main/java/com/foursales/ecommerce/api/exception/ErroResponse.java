package com.foursales.ecommerce.api.exception;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "ErroResponse", description = "Modelo padrão de resposta de erro da API.")
public record ErroResponse(
        @Schema(description = "Código interno que categoriza o erro", example = "VALIDACAO")
        String codigo,

        @Schema(description = "Mensagem legível descrevendo o problema", example = "Existem campos invalidos")
        String mensagem,

        @Schema(description = "Momento em que o erro ocorreu", example = "2024-05-12T10:15:30Z")
        OffsetDateTime timestamp,

        @ArraySchema(arraySchema = @Schema(description = "Lista de campos que apresentaram erro"),
                schema = @Schema(implementation = ErroCampo.class))
        List<ErroCampo> campos
) {}
