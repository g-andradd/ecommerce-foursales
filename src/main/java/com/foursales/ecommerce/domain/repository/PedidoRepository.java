package com.foursales.ecommerce.domain.repository;

import com.foursales.ecommerce.domain.entity.Pedido;
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

    @Query(value = """
        SELECT u.id as usuarioId, SUM(p.total) as totalGasto
          FROM pedidos p\s
          JOIN usuarios u ON u.id = p.usuario_id
         WHERE p.status = 'PAGO'
         GROUP BY u.id
         ORDER BY totalGasto DESC
         LIMIT 5
       \s""", nativeQuery = true)
    List<Object[]> top5UsuariosQueMaisCompraram();

    @Query(value = """
        SELECT u.id as usuarioId, AVG(p.total) as ticketMedio
          FROM pedidos p\s
          JOIN usuarios u ON u.id = p.usuario_id
         WHERE p.status = 'PAGO'
         GROUP BY u.id
       \s""", nativeQuery = true)
    List<Object[]> ticketMedioPorUsuario();

    @Query(value = """
        SELECT IFNULL(SUM(p.total), 0) as totalFaturado
          FROM pedidos p\s
         WHERE p.status = 'PAGO'
         AND p.pago_em IS NOT NULL
         AND YEAR(p.pago_em) = :ano
         AND MONTH(p.pago_em) = :mes
        \s""", nativeQuery = true)
    BigDecimal totalFaturadoNoMes(@Param("ano") int ano, @Param("mes") int mes);

}
