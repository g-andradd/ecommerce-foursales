package com.foursales.ecommerce.api.controller;

import com.foursales.ecommerce.api.dto.pedido.CriarPedidoRequest;
import com.foursales.ecommerce.api.dto.pedido.PedidoResponse;
import com.foursales.ecommerce.application.service.pedido.PedidoService;
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

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoV1Controller {

    private final PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody CriarPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request));
    }

    @PostMapping("/{id}/pagar")
    @PreAuthorize("hasRole('USER')")
    public PedidoResponse pagar(@PathVariable UUID id) {
        return pedidoService.pagar(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<PedidoResponse> meusPedidos(@PageableDefault(size=20, sort="criadoEm", direction= Sort.Direction.DESC) Pageable pageable) {
        return pedidoService.listarDoUsuario(pageable);
    }
}
