-- Garantir exclusão em cascata dos itens ao deletar o pedido (integridade + limpeza)
ALTER TABLE itens_pedido
    DROP FOREIGN KEY fk_itens_pedido;

-- ADD com um NOME NOVO para evitar colisão por cache/ordem de execução
ALTER TABLE itens_pedido
    ADD CONSTRAINT fk_itens_pedido_pedido_id
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
            ON DELETE CASCADE;

-- Total faturado no mês (pago_em) e buscas por PAGO
CREATE INDEX idx_pedidos_pago_em ON pedidos (pago_em);
CREATE INDEX idx_pedidos_status ON pedidos (status);
CREATE INDEX idx_pedidos_status_pagoem ON pedidos (status, pago_em);

-- Checks de sanidade
ALTER TABLE produtos
    ADD CONSTRAINT chk_produtos_preco_nonneg CHECK (preco >= 0),
    ADD CONSTRAINT chk_produtos_qtd_nonneg CHECK (quantidade_estoque >= 0);

ALTER TABLE itens_pedido
    ADD CONSTRAINT chk_itens_quantidade_pos CHECK (quantidade > 0),
    ADD CONSTRAINT chk_itens_preco_unit_nonneg CHECK (preco_unitario >= 0),
    ADD CONSTRAINT chk_itens_subtotal_nonneg CHECK (subtotal >= 0);