DROP VIEW IF EXISTS vw_resumo_projetos;
DROP FUNCTION IF EXISTS fn_total_pago_contrato(INTEGER);
DROP PROCEDURE IF EXISTS sp_aceitar_proposta(INTEGER, DATE);
DROP FUNCTION IF EXISTS fn_validar_pagamento() CASCADE;
DROP FUNCTION IF EXISTS fn_validar_proposta() CASCADE;

DROP TABLE IF EXISTS pagamentos;
DROP TABLE IF EXISTS contratos;
DROP TABLE IF EXISTS propostas;
DROP TABLE IF EXISTS projetos;
DROP TABLE IF EXISTS freelancers;
DROP TABLE IF EXISTS clientes;

CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE freelancers (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    especialidade VARCHAR(80) NOT NULL,
    valor_hora NUMERIC(10, 2) NOT NULL CHECK (valor_hora > 0),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projetos (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    descricao TEXT NOT NULL,
    orcamento NUMERIC(10, 2) NOT NULL CHECK (orcamento > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTO',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_projetos_clientes
        FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT ck_projetos_status
        CHECK (status IN ('ABERTO', 'EM_ANDAMENTO', 'FINALIZADO', 'CANCELADO'))
);

CREATE TABLE propostas (
    id SERIAL PRIMARY KEY,
    projeto_id INTEGER NOT NULL,
    freelancer_id INTEGER NOT NULL,
    valor NUMERIC(10, 2) NOT NULL CHECK (valor > 0),
    prazo_dias INTEGER NOT NULL CHECK (prazo_dias > 0),
    mensagem TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_propostas_projetos
        FOREIGN KEY (projeto_id) REFERENCES projetos(id),
    CONSTRAINT fk_propostas_freelancers
        FOREIGN KEY (freelancer_id) REFERENCES freelancers(id),
    CONSTRAINT ck_propostas_status
        CHECK (status IN ('PENDENTE', 'ACEITA', 'RECUSADA'))
);

CREATE FUNCTION fn_validar_proposta()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_projeto VARCHAR(20);
BEGIN
    SELECT status
    INTO v_status_projeto
    FROM projetos
    WHERE id = NEW.projeto_id;

    IF v_status_projeto <> 'ABERTO' THEN
        RAISE EXCEPTION 'Projeto nao esta aberto para receber propostas';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER tg_validar_proposta
BEFORE INSERT ON propostas
FOR EACH ROW
EXECUTE FUNCTION fn_validar_proposta();

CREATE TABLE contratos (
    id SERIAL PRIMARY KEY,
    proposta_id INTEGER NOT NULL UNIQUE,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    valor_total NUMERIC(10, 2) NOT NULL CHECK (valor_total > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT fk_contratos_propostas
        FOREIGN KEY (proposta_id) REFERENCES propostas(id),
    CONSTRAINT ck_contratos_status
        CHECK (status IN ('ATIVO', 'FINALIZADO', 'CANCELADO'))
);

CREATE TABLE pagamentos (
    id SERIAL PRIMARY KEY,
    contrato_id INTEGER NOT NULL,
    valor NUMERIC(10, 2) NOT NULL CHECK (valor > 0),
    data_pagamento DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PAGO',
    CONSTRAINT fk_pagamentos_contratos
        FOREIGN KEY (contrato_id) REFERENCES contratos(id),
    CONSTRAINT ck_pagamentos_status
        CHECK (status IN ('PENDENTE', 'PAGO', 'CANCELADO'))
);

CREATE FUNCTION fn_validar_pagamento()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_pago NUMERIC(10, 2);
    v_valor_contrato NUMERIC(10, 2);
BEGIN
    SELECT COALESCE(SUM(valor), 0)
    INTO v_total_pago
    FROM pagamentos
    WHERE contrato_id = NEW.contrato_id
      AND status = 'PAGO';

    SELECT valor_total
    INTO v_valor_contrato
    FROM contratos
    WHERE id = NEW.contrato_id;

    IF NEW.status = 'PAGO' AND (v_total_pago + NEW.valor) > v_valor_contrato THEN
        RAISE EXCEPTION 'Pagamento ultrapassa o valor total do contrato';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER tg_validar_pagamento
BEFORE INSERT ON pagamentos
FOR EACH ROW
EXECUTE FUNCTION fn_validar_pagamento();

CREATE VIEW vw_resumo_projetos AS
SELECT
    p.id AS projeto_id,
    p.titulo,
    c.nome AS cliente,
    p.orcamento,
    p.status,
    COUNT(pr.id) AS total_propostas
FROM projetos p
JOIN clientes c ON c.id = p.cliente_id
LEFT JOIN propostas pr ON pr.projeto_id = p.id
GROUP BY p.id, p.titulo, c.nome, p.orcamento, p.status;

CREATE FUNCTION fn_total_pago_contrato(p_contrato_id INTEGER)
RETURNS NUMERIC(10, 2)
LANGUAGE plpgsql
AS $$
DECLARE
    total NUMERIC(10, 2);
BEGIN
    SELECT COALESCE(SUM(valor), 0)
    INTO total
    FROM pagamentos
    WHERE contrato_id = p_contrato_id
      AND status = 'PAGO';

    RETURN total;
END;
$$;

CREATE PROCEDURE sp_aceitar_proposta(p_proposta_id INTEGER, p_data_inicio DATE)
LANGUAGE plpgsql
AS $$
DECLARE
    v_projeto_id INTEGER;
    v_valor NUMERIC(10, 2);
    v_status_projeto VARCHAR(20);
BEGIN
    SELECT pr.projeto_id, pr.valor, p.status
    INTO v_projeto_id, v_valor, v_status_projeto
    FROM propostas pr
    JOIN projetos p ON p.id = pr.projeto_id
    WHERE pr.id = p_proposta_id;

    IF v_projeto_id IS NULL THEN
        RAISE EXCEPTION 'Proposta nao encontrada';
    END IF;

    IF v_status_projeto <> 'ABERTO' THEN
        RAISE EXCEPTION 'Projeto nao esta aberto';
    END IF;

    UPDATE propostas
    SET status = 'ACEITA'
    WHERE id = p_proposta_id;

    UPDATE propostas
    SET status = 'RECUSADA'
    WHERE projeto_id = v_projeto_id
      AND id <> p_proposta_id;

    UPDATE projetos
    SET status = 'EM_ANDAMENTO'
    WHERE id = v_projeto_id;

    INSERT INTO contratos (proposta_id, data_inicio, valor_total, status)
    VALUES (p_proposta_id, p_data_inicio, v_valor, 'ATIVO');
END;
$$;
