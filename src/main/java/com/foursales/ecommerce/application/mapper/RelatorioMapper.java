package com.foursales.ecommerce.application.mapper;

import com.foursales.ecommerce.api.dto.relatorio.TicketMedioResponse;
import com.foursales.ecommerce.api.dto.relatorio.TopCompradorResponse;
import com.foursales.ecommerce.domain.repository.projection.TicketMedioProjection;
import com.foursales.ecommerce.domain.repository.projection.TopCompradorProjection;
import org.springframework.stereotype.Component;

@Component
public class RelatorioMapper {

    public TopCompradorResponse toResponse(TopCompradorProjection projection) {
        return new TopCompradorResponse(projection.getUsuarioId(), projection.getTotalGasto());
    }

    public TicketMedioResponse toResponse(TicketMedioProjection projection) {
        return new TicketMedioResponse(projection.getUsuarioId(), projection.getTicketMedio());
    }

}
