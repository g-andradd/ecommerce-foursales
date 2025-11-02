-- =====================================================================
--  V2__seed.sql
--  Seeds iniciais do banco para testes
-- =====================================================================

-- Helper: função de geração de UUID compatível com BINARY(16)
-- Para inserir: use UNHEX(REPLACE(UUID(), '-', ''))

-- ===================================
--  Usuários (ADMIN + USER)
-- ===================================
INSERT INTO usuarios (id, nome, email, senha, perfil, ativo)
VALUES
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Administrador',
     'admin@demo.com',
     '$2a$10$2bPZ/5p9ZUd6s0b2Y0F7puQmS6b1v8q0o7L9J0QmT0y7r3mS1b0H6',
     'ADMIN',
     TRUE
    ),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'João Silva',
     'joao@demo.com',
     '$2a$10$2bPZ/5p9ZUd6s0b2Y0F7puQmS6b1v8q0o7L9J0QmT0y7r3mS1b0H6',
     'USER',
     TRUE
    ),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Maria Souza',
     'maria@demo.com',
     '$2a$10$2bPZ/5p9ZUd6s0b2Y0F7puQmS6b1v8q0o7L9J0QmT0y7r3mS1b0H6',
     'USER',
     TRUE
    );


-- Senha: “123” (hash BCrypt fixo apenas para testes)
-- ===================================
--  Produtos (estoque inicial)
-- ===================================
INSERT INTO produtos (id, nome, descricao, preco, categoria, quantidade_estoque)
VALUES
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Camiseta Basica',
     'Camiseta 100% algodao, unissex, varias cores',
     59.90,
     'Vestuário',
     120),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Calça Jeans Slim',
     'Modelagem slim com elastano, lavagem escura',
     179.90,
     'Vestuário',
     80),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Tenis Confort',
     'Tenis esportivo leve e respiravel, varias cores',
     299.90,
     'Calçados',
     50),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Relogio Digital',
     'Relogio digital a prova d’água com cronometro e luz de fundo',
     249.90,
     'Acessórios',
     30),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Mochila Casual',
     'Mochila com compartimento para notebook de até 15 polegadas',
     199.90,
     'Acessórios',
     40),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Fone Bluetooth',
     'Fone sem fio com cancelamento de ruido e bateria de longa duração',
     349.90,
     'Eletrônicos',
     25),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Mouse Gamer',
     'Mouse com 6 botoes programaveis e sensor de alta precisão',
     159.90,
     'Eletrônicos',
     60),
    (UNHEX(REPLACE(UUID(), '-', '')),
     'Cadeira Ergonomica',
     'Cadeira giratoria com apoio lombar e regulagem de altura',
     899.90,
     'Móveis',
     15);

-- ===================================
--  Pedidos de exemplo (opcional)
-- ===================================
-- Apenas se quiser popular o sistema para testes manuais
-- Todos iniciam com status PENDENTE
-- Usuário: Joao Silva
-- Produtos: Camiseta + Tenis

-- INSERT INTO pedidos (id, usuario_id, status, total)
-- VALUES (UNHEX(REPLACE(UUID(), '-', '')),
--         (SELECT id FROM usuarios WHERE email='joao@demo.com'),
--         'PENDENTE', 0);

-- INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal)
-- VALUES (
--   (SELECT id FROM pedidos WHERE usuario_id = (SELECT id FROM usuarios WHERE email='joao@demo.com') LIMIT 1),
--   (SELECT id FROM produtos WHERE nome='Camiseta Basica'),
--   2, 59.90, 119.80
-- ),
-- (
--   (SELECT id FROM pedidos WHERE usuario_id = (SELECT id FROM usuarios WHERE email='joao@demo.com') LIMIT 1),
--   (SELECT id FROM produtos WHERE nome='Tenis Confort'),
--   1, 299.90, 299.90
-- );

-- Total calculado dinamicamente pela aplicação no pagamento.
