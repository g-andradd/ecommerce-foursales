package com.foursales.ecommerce.domain.repository;

import com.foursales.ecommerce.domain.entity.Produto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produto p where p.id in :ids")
    List<Produto> findAllByIdForUpdate(@Param("ids") Collection<UUID> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update Produto p\s
              set p.quantidadeEstoque = p.quantidadeEstoque - :qtd
            where p.id = :produtoId
              and p.quantidadeEstoque >= :qtd
          \s""")
    int decrementarEstoqueSeDisponivel(@Param("produtoId") UUID produtoId, @Param("qtd") int qtd);
}
