package com.foursales.ecommerce.application.service;

import com.foursales.ecommerce.api.dto.relatorio.FaturadoMesResponse;
import com.foursales.ecommerce.api.dto.relatorio.TicketMedioResponse;
import com.foursales.ecommerce.api.dto.relatorio.TopCompradorResponse;
import com.foursales.ecommerce.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PedidoRepository pedidoRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<TopCompradorResponse> top5UsuariosQueMaisCompraram() {
        return pedidoRepository.top5UsuariosQueMaisCompraram().stream()
                .map(row -> new TopCompradorResponse(toUuid(row[0]), toBigDecimal(row[1])))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketMedioResponse> ticketMedioPorUsuario() {
        return pedidoRepository.ticketMedioPorUsuario().stream()
                .map(row -> new TicketMedioResponse(toUuid(row[0]), toBigDecimal(row[1])))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public FaturadoMesResponse totalFaturadoNoMes(int ano, int mes) {
        BigDecimal total = Optional.ofNullable(pedidoRepository.totalFaturadoNoMes(ano, mes))
                .orElse(BigDecimal.ZERO);
        return new FaturadoMesResponse(ano, mes, total);
    }

    private UUID toUuid(Object o) {
        if (o instanceof UUID u) return u;
        if (o instanceof byte[] b) {
            ByteBuffer bb = ByteBuffer.wrap(b);
            long most = bb.getLong();
            long least = bb.getLong();
            return new UUID(most, least);
        }
        throw new IllegalArgumentException("Tipo inesperado para UUID: " + o.getClass());
    }

    private BigDecimal toBigDecimal(Object o){ return new BigDecimal(o.toString()); }

}
