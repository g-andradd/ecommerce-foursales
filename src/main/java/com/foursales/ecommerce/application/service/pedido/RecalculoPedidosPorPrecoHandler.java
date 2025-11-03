package com.foursales.ecommerce.application.service.pedido;

import com.foursales.ecommerce.application.service.produto.ProdutoPrecoAtualizadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RecalculoPedidosPorPrecoHandler {

    private final RecalculoPedidosTxService txService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProdutoAtualizado(ProdutoPrecoAtualizadoEvent event) {

        txService.recalcularPedidosPendentesParaProduto(event.produtoId(), event.novoPreco());

    }

}
