INSERT INTO clientes (nome, email, telefone) VALUES
('Maria Oliveira', 'maria@empresa.com', '11999990000'),
('Carlos Souza', 'carlos@loja.com', '11988880000');

INSERT INTO freelancers (nome, email, especialidade, valor_hora) VALUES
('Joao Curcino', 'joao@email.com', 'Backend Java e APIs', 45.00),
('Ana Lima', 'ana@email.com', 'Frontend React', 55.00),
('Pedro Santos', 'pedro@email.com', 'Mobile', 60.00);

INSERT INTO projetos (cliente_id, titulo, descricao, orcamento) VALUES
(1, 'Sistema de pedidos online', 'Criar um sistema simples para pedidos de uma loja.', 3000.00),
(2, 'Landing page institucional', 'Criar uma pagina para apresentar a empresa.', 1200.00),
(1, 'API de integracao financeira', 'Criar API para integrar pagamentos.', 4500.00);

INSERT INTO propostas (projeto_id, freelancer_id, valor, prazo_dias, mensagem) VALUES
(1, 1, 2800.00, 20, 'Consigo desenvolver usando Java e PostgreSQL.'),
(1, 2, 3200.00, 25, 'Posso ajudar com frontend e interface.'),
(2, 2, 1100.00, 10, 'Tenho experiencia com React e Tailwind.'),
(3, 1, 4300.00, 30, 'Tenho experiencia com APIs e integracoes.');

UPDATE freelancers
SET valor_hora = 50.00
WHERE id = 1;

DELETE FROM propostas
WHERE id = 2;

SELECT
    p.titulo,
    c.nome AS cliente,
    p.orcamento
FROM projetos p
JOIN clientes c ON c.id = p.cliente_id;

SELECT
    p.titulo,
    f.nome AS freelancer,
    pr.valor,
    pr.status
FROM propostas pr
JOIN projetos p ON p.id = pr.projeto_id
JOIN freelancers f ON f.id = pr.freelancer_id;

SELECT
    co.id AS contrato_id,
    p.titulo,
    c.nome AS cliente,
    f.nome AS freelancer,
    co.valor_total
FROM contratos co
JOIN propostas pr ON pr.id = co.proposta_id
JOIN projetos p ON p.id = pr.projeto_id
JOIN clientes c ON c.id = p.cliente_id
JOIN freelancers f ON f.id = pr.freelancer_id;

CALL sp_aceitar_proposta(1, CURRENT_DATE);

INSERT INTO pagamentos (contrato_id, valor, data_pagamento, status)
VALUES (1, 1000.00, CURRENT_DATE, 'PAGO');

SELECT fn_total_pago_contrato(1) AS total_pago;

SELECT * FROM vw_resumo_projetos;
