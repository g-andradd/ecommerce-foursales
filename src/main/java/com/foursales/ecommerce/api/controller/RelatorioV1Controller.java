package com.foursales.ecommerce.api.controller;

import com.foursales.ecommerce.api.dto.relatorio.FaturadoMesResponse;
import com.foursales.ecommerce.api.dto.relatorio.TicketMedioResponse;
import com.foursales.ecommerce.api.dto.relatorio.TopCompradorResponse;
import com.foursales.ecommerce.api.exception.ErroResponse;
import com.foursales.ecommerce.application.service.RelatorioService;
import com.foursales.ecommerce.infra.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Relatórios", description = "Indicadores de negócio do e-commerce")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
public class RelatorioV1Controller {

    private final RelatorioService relatorioService;

    @Operation(summary = "Top compradores",
            description = "Recupera os cinco usuários que mais compraram no período completo disponível.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relação de usuários",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TopCompradorResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/top-usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopCompradorResponse> top5() { return relatorioService.top5UsuariosQueMaisCompraram(); }

    @Operation(summary = "Ticket médio por usuário",
            description = "Retorna o ticket médio de compras por usuário registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket médio por usuário",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TicketMedioResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/ticket-medio")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketMedioResponse> ticketMedio() { return relatorioService.ticketMedioPorUsuario(); }

    @Operation(summary = "Faturamento mensal",
            description = "Calcula o valor total faturado para o mês e ano informados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Faturamento consolidado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FaturadoMesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/faturado")
    @PreAuthorize("hasRole('ADMIN')")
    public FaturadoMesResponse faturado(@RequestParam int ano, @RequestParam int mes) {
        return relatorioService.totalFaturadoNoMes(ano, mes);
    }

}
