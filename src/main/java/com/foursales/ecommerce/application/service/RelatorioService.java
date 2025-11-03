package com.foursales.ecommerce.application.service;

import com.foursales.ecommerce.api.dto.relatorio.FaturadoMesResponse;
import com.foursales.ecommerce.api.dto.relatorio.TicketMedioResponse;
import com.foursales.ecommerce.api.dto.relatorio.TopCompradorResponse;
import com.foursales.ecommerce.application.mapper.RelatorioMapper;
import com.foursales.ecommerce.domain.entity.StatusPedido;
import com.foursales.ecommerce.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PedidoRepository pedidoRepository;
    private final RelatorioMapper relatorioMapper;

    private static final StatusPedido STATUS_PAGO = StatusPedido.PAGO;
    private static final int TOP_COMPRADORES_LIMITE = 5;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<TopCompradorResponse> top5UsuariosQueMaisCompraram() {
        var pageable = PageRequest.of(0, TOP_COMPRADORES_LIMITE);
        return pedidoRepository.buscarTopCompradores(STATUS_PAGO, pageable).stream()
                .map(relatorioMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<TicketMedioResponse> ticketMedioPorUsuario() {
        return pedidoRepository.calcularTicketMedioPorUsuario(STATUS_PAGO).stream()
                .map(relatorioMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public FaturadoMesResponse totalFaturadoNoMes(int ano, int mes) {
        BigDecimal total = pedidoRepository.totalFaturadoNoMes(STATUS_PAGO, ano, mes);
        return new FaturadoMesResponse(ano, mes, total);
    }

}
