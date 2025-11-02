package com.foursales.ecommerce.api.controller;

import com.foursales.ecommerce.api.dto.relatorio.FaturadoMesResponse;
import com.foursales.ecommerce.api.dto.relatorio.TicketMedioResponse;
import com.foursales.ecommerce.api.dto.relatorio.TopCompradorResponse;
import com.foursales.ecommerce.application.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioV1Controller {

    private final RelatorioService relatorioService;

    @GetMapping("/top-usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopCompradorResponse> top5() { return relatorioService.top5UsuariosQueMaisCompraram(); }

    @GetMapping("/ticket-medio")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketMedioResponse> ticketMedio() { return relatorioService.ticketMedioPorUsuario(); }

    @GetMapping("/faturado")
    @PreAuthorize("hasRole('ADMIN')")
    public FaturadoMesResponse faturado(@RequestParam int ano, @RequestParam int mes) {
        return relatorioService.totalFaturadoNoMes(ano, mes);
    }

}
