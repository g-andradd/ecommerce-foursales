package com.foursales.ecommerce.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_pedido")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="pedido_id", nullable=false)
    private Pedido pedido;

    @Column(columnDefinition = "BINARY(16)", name="produto_id", nullable=false)
    private UUID produtoId;

    @NotNull
    @Column(nullable=false)
    private Integer quantidade;

    @NotNull
    @Column(name="preco_unitario", nullable=false, precision=15, scale=2)
    private BigDecimal precoUnitario;

    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal subtotal;

    public static ItemPedido of(UUID produtoId, Integer qtd, BigDecimal precoAtual) {
        return ItemPedido.builder()
                .produtoId(produtoId)
                .quantidade(qtd)
                .precoUnitario(precoAtual)
                .subtotal(precoAtual.multiply(BigDecimal.valueOf(qtd)))
                .build();
    }

}
