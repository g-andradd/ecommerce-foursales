package com.foursales.ecommerce.application.service;

import com.foursales.ecommerce.api.dto.relatorio.FaturadoMesResponse;
import com.foursales.ecommerce.api.dto.relatorio.TicketMedioResponse;
import com.foursales.ecommerce.api.dto.relatorio.TopCompradorResponse;
import com.foursales.ecommerce.application.mapper.RelatorioMapper;
import com.foursales.ecommerce.domain.entity.StatusPedido;
import com.foursales.ecommerce.domain.repository.PedidoRepository;
import com.foursales.ecommerce.domain.repository.projection.TicketMedioProjection;
import com.foursales.ecommerce.domain.repository.projection.TopCompradorProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    private RelatorioService service;

    @BeforeEach
    void setUp() {
        service = new RelatorioService(pedidoRepository, new RelatorioMapper());
    }

    @Test
    void top5UsuariosQueMaisCompraramDeveRetornarResponses() {
        UUID usuarioId = UUID.randomUUID();
        TopCompradorProjection projection = new TopCompradorProjection() {
            @Override
            public UUID getUsuarioId() {
                return usuarioId;
            }

            @Override
            public BigDecimal getTotalGasto() {
                return new BigDecimal("500.00");
            }
        };

        when(pedidoRepository.buscarTopCompradores(eq(StatusPedido.PAGO), any(PageRequest.class)))
                .thenReturn(List.of(projection));

        List<TopCompradorResponse> responses = service.top5UsuariosQueMaisCompraram();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).usuarioId()).isEqualTo(usuarioId);
        assertThat(responses.get(0).totalGasto()).isEqualByComparingTo("500.00");
        verify(pedidoRepository).buscarTopCompradores(eq(StatusPedido.PAGO), any(PageRequest.class));
    }

    @Test
    void ticketMedioPorUsuarioDeveConverterProjecoes() {
        UUID usuarioId = UUID.randomUUID();
        TicketMedioProjection projection = new TicketMedioProjection() {
            @Override
            public UUID getUsuarioId() {
                return usuarioId;
            }

            @Override
            public BigDecimal getTicketMedio() {
                return new BigDecimal("150.00");
            }
        };

        when(pedidoRepository.calcularTicketMedioPorUsuario(StatusPedido.PAGO)).thenReturn(List.of(projection));

        List<TicketMedioResponse> responses = service.ticketMedioPorUsuario();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).usuarioId()).isEqualTo(usuarioId);
        assertThat(responses.get(0).ticketMedio()).isEqualByComparingTo("150.00");
        verify(pedidoRepository).calcularTicketMedioPorUsuario(StatusPedido.PAGO);
    }

    @Test
    void totalFaturadoNoMesDeveConsultarRepositorio() {
        when(pedidoRepository.totalFaturadoNoMes(StatusPedido.PAGO, 2025, 1)).thenReturn(new BigDecimal("1234.56"));

        FaturadoMesResponse response = service.totalFaturadoNoMes(2025, 1);

        assertThat(response.ano()).isEqualTo(2025);
        assertThat(response.mes()).isEqualTo(1);
        assertThat(response.totalFaturado()).isEqualByComparingTo("1234.56");
        verify(pedidoRepository).totalFaturadoNoMes(StatusPedido.PAGO, 2025, 1);
    }

}