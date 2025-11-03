package com.foursales.ecommerce.api.controller;

import com.foursales.ecommerce.api.dto.pedido.CriarPedidoRequest;
import com.foursales.ecommerce.api.dto.pedido.PedidoResponse;
import com.foursales.ecommerce.api.exception.ErroResponse;
import com.foursales.ecommerce.application.service.pedido.PedidoService;
import com.foursales.ecommerce.infra.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Pedidos", description = "Operações relacionadas aos pedidos dos clientes")
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
public class PedidoV1Controller {

    private final PedidoService pedidoService;

    @Operation(summary = "Cria um pedido", description = "Gera um novo pedido para o usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody CriarPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request));
    }

    @Operation(summary = "Realiza o pagamento de um pedido",
            description = "Atualiza o status do pedido para pago, caso esteja elegível.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido pago",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "422", description = "Pedido em estado inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping("/{id}/pagar")
    @PreAuthorize("hasRole('USER')")
    public PedidoResponse pagar(@PathVariable UUID id) {
        return pedidoService.pagar(id);
    }

    @Operation(summary = "Lista pedidos do usuário",
            description = "Retorna os pedidos do usuário autenticado, ordenados por data de criação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos retornados",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<PedidoResponse> meusPedidos(@PageableDefault(size=20, sort="criadoEm", direction= Sort.Direction.DESC) Pageable pageable) {
        return pedidoService.listarDoUsuario(pageable);
    }
}
