package com.foursales.ecommerce.domain.repository;

import com.foursales.ecommerce.domain.entity.ItemPedido;
import com.foursales.ecommerce.domain.entity.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    @Query("""
      select distinct i.pedido.id
      from ItemPedido i
      where i.produtoId = :produtoId
        and i.pedido.status = :status
    """)
    List<UUID> listarPedidosPendentesComProduto(@Param("produtoId") UUID produtoId,
                                                @Param("status") StatusPedido status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      update ItemPedido i
         set i.precoUnitario = :novoPreco,
             i.subtotal = :novoPreco * i.quantidade
       where i.produtoId = :produtoId
         and i.pedido.id in (
              select p.id from Pedido p where p.status = :status
         )
    """)
    int atualizarPrecoItensEmPedidosPendentes(@Param("produtoId") UUID produtoId,
                                              @Param("novoPreco") java.math.BigDecimal novoPreco,
                                              @Param("status") StatusPedido status);

    @Query("""
      select i.pedido.id as pedidoId, sum(i.subtotal) as total
      from ItemPedido i
      where i.pedido.id in :pedidoIds
      group by i.pedido.id
    """)
    List<Object[]> somarSubtotalPorPedido(@Param("pedidoIds") Collection<UUID> pedidoIds);

}
