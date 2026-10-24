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

-- =====================================================
-- Dados adicionais para os relatorios gerenciais
-- =====================================================

INSERT INTO clientes (nome, email, telefone) VALUES
('Fernanda Rocha', 'fernanda@studio.com', '11977770000');

INSERT INTO freelancers (nome, email, especialidade, valor_hora) VALUES
('Beatriz Costa', 'beatriz@email.com', 'Design UI/UX', 70.00);

INSERT INTO projetos (cliente_id, titulo, descricao, orcamento) VALUES
(2, 'Aplicativo de agendamento', 'Criar um app para agendar servicos da loja.', 5000.00),
(3, 'Loja virtual de roupas', 'Criar uma loja virtual com catalogo e carrinho.', 3500.00);

INSERT INTO propostas (projeto_id, freelancer_id, valor, prazo_dias, mensagem) VALUES
(4, 3, 4800.00, 35, 'Desenvolvo o app em Flutter para Android e iOS.'),
(4, 2, 5200.00, 30, 'Posso fazer a interface e o painel web.'),
(2, 3, 1300.00, 12, 'Entrego a landing page responsiva.'),
(5, 2, 3300.00, 25, 'Monto a loja com React e integracao de pagamento.'),
(5, 4, 2900.00, 20, 'Faco o design completo da loja e do checkout.');

-- Aceita propostas: cada chamada cria um contrato e recusa as demais do projeto
CALL sp_aceitar_proposta(3, CURRENT_DATE);  -- contrato 2: landing page (Ana)
CALL sp_aceitar_proposta(5, CURRENT_DATE);  -- contrato 3: app de agendamento (Pedro)
CALL sp_aceitar_proposta(4, CURRENT_DATE);  -- contrato 4: API financeira (Joao)

INSERT INTO pagamentos (contrato_id, valor, data_pagamento, status) VALUES
(2, 1100.00, CURRENT_DATE, 'PAGO'),
(3, 2000.00, CURRENT_DATE, 'PAGO'),
(3, 1500.00, CURRENT_DATE, 'PAGO'),
(4, 1000.00, CURRENT_DATE, 'PAGO');

-- =====================================================
-- Consultas com GROUP BY, HAVING e ORDER BY
-- =====================================================

-- Consulta 1 (consulta integrada): freelancers mais ativos.
-- Necessidade: identificar os profissionais que mais participam da plataforma.
-- JOIN liga freelancers as suas propostas, GROUP BY agrupa por freelancer,
-- COUNT/AVG calculam quantidade e valor medio, HAVING mantem apenas quem
-- enviou pelo menos 2 propostas e ORDER BY coloca os mais ativos primeiro.
SELECT
    f.nome AS freelancer,
    f.especialidade,
    COUNT(pr.id) AS total_propostas,
    ROUND(AVG(pr.valor), 2) AS valor_medio
FROM freelancers f
JOIN propostas pr ON pr.freelancer_id = f.id
GROUP BY f.id, f.nome, f.especialidade
HAVING COUNT(pr.id) >= 2
ORDER BY total_propostas DESC, valor_medio DESC;

-- Consulta 2: clientes que mais investem na plataforma.
-- Necessidade: encontrar os clientes com maior volume contratado.
-- WHERE descarta contratos cancelados ANTES do agrupamento;
-- HAVING filtra os grupos (clientes) com total contratado >= R$ 6.000.
SELECT
    c.nome AS cliente,
    COUNT(co.id) AS total_contratos,
    SUM(co.valor_total) AS total_contratado
FROM clientes c
JOIN projetos p ON p.cliente_id = c.id
JOIN propostas pr ON pr.projeto_id = p.id
JOIN contratos co ON co.proposta_id = pr.id
WHERE co.status <> 'CANCELADO'
GROUP BY c.id, c.nome
HAVING SUM(co.valor_total) >= 6000
ORDER BY total_contratado DESC;

-- Consulta 3: contratos com saldo a pagar.
-- Necessidade: mostrar quais contratos ainda tem valores pendentes.
-- LEFT JOIN mantem contratos sem pagamento; HAVING compara o total pago
-- com o valor do contrato; ORDER BY mostra primeiro o maior saldo.
SELECT
    co.id AS contrato_id,
    p.titulo AS projeto,
    f.nome AS freelancer,
    co.valor_total,
    COALESCE(SUM(pg.valor), 0) AS total_pago,
    co.valor_total - COALESCE(SUM(pg.valor), 0) AS saldo
FROM contratos co
JOIN propostas pr ON pr.id = co.proposta_id
JOIN projetos p ON p.id = pr.projeto_id
JOIN freelancers f ON f.id = pr.freelancer_id
LEFT JOIN pagamentos pg ON pg.contrato_id = co.id AND pg.status = 'PAGO'
GROUP BY co.id, p.titulo, f.nome, co.valor_total
HAVING COALESCE(SUM(pg.valor), 0) < co.valor_total
ORDER BY saldo DESC;
