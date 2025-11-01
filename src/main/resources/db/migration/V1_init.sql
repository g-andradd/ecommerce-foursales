CREATE TABLE usuarios (
    id BINARY(16) PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    senha VARCHAR(120) NOT NULL,
    papel ENUM('ADMIN','USER') NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE produtos (
    id BINARY(16) PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco DECIMAL(12,2) NOT NULL,
    categoria VARCHAR(80),
    quantidade_estoque INT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_produtos_nome (nome),
    INDEX idx_produtos_categoria (categoria)
) ENGINE=InnoDB;


CREATE TABLE pedidos (
    id BINARY(16) PRIMARY KEY,
    usuario_id BINARY(16) NOT NULL,
    status ENUM('PENDENTE','PAGO','CANCELADO') NOT NULL,
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pago_em TIMESTAMP NULL,
    CONSTRAINT fk_pedidos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_pedidos_usuario (usuario_id),
    INDEX idx_pedidos_status_criado (status, criado_em)
) ENGINE=InnoDB;


CREATE TABLE itens_pedido (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pedido_id BINARY(16) NOT NULL,
    produto_id BINARY(16) NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_itens_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_itens_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
    INDEX idx_itens_pedido (pedido_id),
    INDEX idx_itens_produto (produto_id)
) ENGINE=InnoDB;
