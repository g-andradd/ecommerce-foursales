package com.foursales.ecommerce.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)", name="usuario_id", nullable=false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatusPedido status;

    @NotNull
    @Column(nullable=false, precision=15, scale=2)
    private BigDecimal total;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "pago_em", nullable = false, updatable = false)
    private LocalDateTime pagoEm;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    @PrePersist void prePersist() {
        total = BigDecimal.ZERO;
        if (status == null) status = StatusPedido.PENDENTE;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
        recalcularTotal();
    }

    public void recalcularTotal() {
        this.total = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void marcarPago() {
        this.status = StatusPedido.PAGO;
        this.pagoEm = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = StatusPedido.CANCELADO;
    }

}
