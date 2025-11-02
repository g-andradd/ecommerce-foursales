package com.foursales.ecommerce.application.service.pedido;

import com.foursales.ecommerce.domain.entity.Pedido;
import com.foursales.ecommerce.domain.entity.StatusPedido;
import com.foursales.ecommerce.domain.repository.ItemPedidoRepository;
import com.foursales.ecommerce.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecalculoPedidosTxService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recalcularPedidosPendentesParaProduto(UUID produtoId, BigDecimal novoPreco) {
        List<UUID> pedidoIds = itemPedidoRepository
                .listarPedidosPendentesComProduto(produtoId, StatusPedido.PENDENTE);
        if (pedidoIds.isEmpty()) return;

        List<Pedido> pedidosLocked = pedidoRepository.findAllByIdForUpdate(pedidoIds);
        if (pedidosLocked.isEmpty()) return;

        itemPedidoRepository.atualizarPrecoItensEmPedidosPendentes(
                produtoId, novoPreco, StatusPedido.PENDENTE);

        List<Object[]> somas = itemPedidoRepository.somarSubtotalPorPedido(pedidoIds);

        Map<UUID, BigDecimal> totalPorPedido = new HashMap<>();
        for (Object[] row : somas) {
            UUID pid = (UUID) row[0];
            BigDecimal total = (BigDecimal) row[1];
            totalPorPedido.put(pid, total);
        }

        for (Pedido p : pedidosLocked) {
            p.setTotal(totalPorPedido.getOrDefault(p.getId(), BigDecimal.ZERO));
        }
        pedidoRepository.saveAll(pedidosLocked);
    }

}
