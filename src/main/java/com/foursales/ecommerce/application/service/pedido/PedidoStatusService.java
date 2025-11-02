package com.foursales.ecommerce.application.service.pedido;

import com.foursales.ecommerce.domain.entity.Pedido;
import com.foursales.ecommerce.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoStatusService {

    private final PedidoRepository pedidoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarCancelado(Pedido pedido) {
        pedido.cancelar();
        pedidoRepository.save(pedido);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void marcarPago(Pedido pedido) {
        pedido.marcarPago();
        pedidoRepository.save(pedido);
    }
}
