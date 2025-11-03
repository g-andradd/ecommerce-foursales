package com.foursales.ecommerce.domain.repository;

import com.foursales.ecommerce.domain.entity.Pedido;
import com.foursales.ecommerce.domain.entity.StatusPedido;
import com.foursales.ecommerce.domain.repository.projection.TicketMedioProjection;
import com.foursales.ecommerce.domain.repository.projection.TopCompradorProjection;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Query("select p from Pedido p where p.usuarioId = :usuarioId order by p.criadoEm desc")
    Page<Pedido> findByUsuarioId(UUID usuarioId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id in :ids")
    List<Pedido> findAllByIdForUpdate(@Param("ids") Collection<UUID> ids);

    @Query("""
        SELECT p.usuarioId AS usuarioId,
               SUM(p.total) AS totalGasto
          FROM Pedido p
         WHERE p.status = :statusPago
         GROUP BY p.usuarioId
         ORDER BY SUM(p.total) DESC
        """)
    Page<TopCompradorProjection> buscarTopCompradores(@Param("statusPago") StatusPedido statusPago, Pageable pageable);

    @Query("""
        SELECT p.usuarioId AS usuarioId,
               AVG(p.total) AS ticketMedio
          FROM Pedido p
         WHERE p.status = :statusPago
         GROUP BY p.usuarioId
        """)
    List<TicketMedioProjection> calcularTicketMedioPorUsuario(@Param("statusPago") StatusPedido statusPago);

    @Query("""
        SELECT COALESCE(SUM(p.total), 0) as totalFaturado
          FROM Pedido p
         WHERE p.status = :statusPago
           AND (
                (p.pagoEm IS NOT NULL
                 AND FUNCTION('YEAR', p.pagoEm) = :ano
                 AND FUNCTION('MONTH', p.pagoEm) = :mes)
             OR (p.pagoEm IS NULL
                 AND FUNCTION('YEAR', p.criadoEm) = :ano
                 AND FUNCTION('MONTH', p.criadoEm) = :mes)
           )
        """)
    BigDecimal totalFaturadoNoMes(@Param("statusPago") StatusPedido statusPago,
                                  @Param("ano") int ano,
                                  @Param("mes") int mes);

}
