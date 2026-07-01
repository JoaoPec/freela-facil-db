# Freela Fácil — Resumo para Apresentação

## O que é o sistema?

Plataforma de freelancers. O cliente publica um projeto, o freelancer envia proposta, o cliente aceita uma proposta, gera contrato e registra pagamentos.

**6 tabelas:** `clientes`, `freelancers`, `projetos`, `propostas`, `contratos`, `pagamentos`

---

## View — `vw_resumo_projetos`

| | |
|---|---|
| **O que é** | Consulta pronta, como se fosse uma tabela virtual |
| **O que faz** | Mostra cada projeto com nome do cliente e quantas propostas recebeu |
| **SQL** | `SELECT * FROM vw_resumo_projetos;` |
| **No Java** | Opção **14** do menu → `ProjetoDAO.listarViewResumo()` |

> **Frase para falar:** "A view junta projetos com clientes e conta as propostas sem repetir o JOIN toda vez."

---

## Function — `fn_total_pago_contrato`

| | |
|---|---|
| **O que é** | Função que calcula e devolve um número |
| **O que faz** | Soma todos os pagamentos com status `PAGO` de um contrato |
| **SQL** | `SELECT fn_total_pago_contrato(1);` |
| **No Java** | Opção **13** do menu → `PagamentoDAO.mostrarTotalPago()` |

> **Frase para falar:** "A function recebe o ID do contrato e retorna quanto já foi pago."

---

## Procedure — `sp_aceitar_proposta`

| | |
|---|---|
| **O que é** | Rotina que executa vários comandos de uma vez |
| **SQL** | `CALL sp_aceitar_proposta(1, CURRENT_DATE);` |
| **No Java** | Opção **11** do menu → `PropostaDAO.aceitarProposta()` |

**Quando aceita uma proposta, ela:**

1. Marca a proposta escolhida como `ACEITA`
2. Recusa as outras propostas do mesmo projeto
3. Muda o projeto para `EM_ANDAMENTO`
4. Cria o contrato com o valor da proposta

> **Frase para falar:** "A procedure automatiza todo o processo de aceitar proposta em um único comando."

---

## Triggers (gatilhos automáticos)

**O que é:** regra que o banco executa sozinho antes de inserir um dado.

### 1. `tg_validar_proposta` (`fn_validar_proposta`)

- **Dispara:** antes de inserir proposta
- **Regra:** só aceita proposta se o projeto estiver `ABERTO`
- **Se não:** dá erro e não deixa cadastrar
- **No Java:** opção **9** — o Java só faz `INSERT`, o trigger roda no banco

### 2. `tg_validar_pagamento` (`fn_validar_pagamento`)

- **Dispara:** antes de inserir pagamento
- **Regra:** não deixa pagar mais do que o valor total do contrato
- **Se passar:** dá erro e não deixa cadastrar
- **No Java:** opção **12** — o Java só faz `INSERT`, o trigger roda no banco

> **Frase para falar:** "Os triggers são validações automáticas do banco, como um segurança que impede dados inválidos."

---

## Diferença rápida (decorar)

| Objeto | Faz o quê? |
|--------|------------|
| **VIEW** | Consulta pronta (só lê) |
| **FUNCTION** | Calcula e retorna valor |
| **PROCEDURE** | Executa ações (UPDATE, INSERT…) |
| **TRIGGER** | Valida automaticamente antes de salvar |

---

## Estrutura do código Java

```
Usuário (teclado)  →  Main.java (menu)  →  *DAO.java (SQL)  →  PostgreSQL
```

### Pasta `src/`

| Arquivo | Função |
|---------|--------|
| `Main.java` | Menu do sistema. Não tem SQL — só chama os DAOs |
| `Conexao.java` | Abre conexão com o PostgreSQL (Railway) |
| `ClienteDAO.java` | CRUD da tabela `clientes` (opções 1–4) |
| `ProjetoDAO.java` | CRUD de `projetos` + JOIN (6) + VIEW (14) |
| `PropostaDAO.java` | Propostas + JOIN (10) + PROCEDURE (11) |
| `PagamentoDAO.java` | Pagamentos + FUNCTION (13) |
| `SetupDatabase.java` | Roda os scripts SQL no banco (utilitário) |
| `TesteConexao.java` | Testa se a conexão funciona (utilitário) |

### Outras pastas

| Pasta | Conteúdo |
|-------|----------|
| `database/01_ddl.sql` | Tabelas, triggers, view, function, procedure |
| `database/02_dml.sql` | Dados de exemplo |
| `config/database.properties` | URL, usuário e senha do Railway |

### O que não tem código Java (fica só no banco)

- `tg_validar_proposta` → `01_ddl.sql`
- `tg_validar_pagamento` → `01_ddl.sql`

---

## Mapa: menu → arquivo → método

| Opção | Arquivo | Método | Recurso SQL |
|-------|---------|--------|-------------|
| 1 | `ClienteDAO` | `inserir` | INSERT |
| 2 | `ClienteDAO` | `listar` | SELECT |
| 3 | `ClienteDAO` | `atualizarEmail` | UPDATE |
| 4 | `ClienteDAO` | `excluir` | DELETE |
| 5 | `ProjetoDAO` | `inserir` | INSERT |
| 6 | `ProjetoDAO` | `listarComCliente` | JOIN |
| 7 | `ProjetoDAO` | `atualizarStatus` | UPDATE |
| 8 | `ProjetoDAO` | `excluir` | DELETE |
| 9 | `PropostaDAO` | `inserir` | INSERT + trigger |
| 10 | `PropostaDAO` | `listarComProjetoEFreelancer` | JOIN |
| 11 | `PropostaDAO` | `aceitarProposta` | **PROCEDURE** |
| 12 | `PagamentoDAO` | `inserir` | INSERT + trigger |
| 13 | `PagamentoDAO` | `mostrarTotalPago` | **FUNCTION** |
| 14 | `ProjetoDAO` | `listarViewResumo` | **VIEW** |

---

## Ordem para demonstrar na apresentação

1. Mostrar diagrama ER (`docs/modelo-dados.html`)
2. Rodar `SetupDatabase` ou mostrar scripts SQL
3. Rodar `Main.java` e listar clientes/projetos (CRUD)
4. Mostrar JOIN (opção 10)
5. Mostrar view (opção 14)
6. Aceitar proposta com procedure (opção 11)
7. Registrar pagamento (opção 12)
8. Mostrar function total pago (opção 13)
9. Explicar triggers como regras automáticas do banco

---

## Como rodar

```powershell
cd freela-facil-db

# Compilar
javac -encoding UTF-8 -cp "lib/postgresql-42.7.4.jar" -d out src/*.java

# Rodar o sistema
java -cp "out;lib/postgresql-42.7.4.jar" Main
```

**Banco:** PostgreSQL no Railway (`config/database.properties`)

---

## Roteiro de demonstração (passo a passo)

Use depois de rodar `SetupDatabase` (banco já com dados de exemplo).

### Antes de abrir o Java (30 seg)

1. Abrir `docs/modelo-dados.html` — mostrar as 6 tabelas e relacionamentos
2. Dizer: *"O banco tem view, function, procedure e triggers no `01_ddl.sql`"*

### No terminal — rodar o sistema

```powershell
java -cp "out;lib/postgresql-42.7.4.jar" Main
```

---

### Passo 1 — Mostrar que o banco tem dados (JOIN)

**Opção 6** — Listar projetos com cliente

- Mostra: título do projeto + nome do cliente + orçamento + status
- **Falar:** *"Aqui uso JOIN para juntar projetos com clientes."*

---

### Passo 2 — Mostrar propostas (JOIN)

**Opção 10** — Listar propostas com JOIN

- Mostra: projeto + freelancer + valor + status
- **Falar:** *"Cada proposta liga um freelancer a um projeto."*

---

### Passo 3 — Mostrar a VIEW

**Opção 14** — Listar view de resumo

- Mostra: projeto + cliente + **total de propostas**
- **Falar:** *"A view já traz o resumo pronto, sem montar a consulta toda vez."*

---

### Passo 4 — Aceitar proposta (PROCEDURE)

**Opção 11** — Aceitar proposta usando procedure

- Digitar: `1` (ID da proposta do João no projeto "Sistema de pedidos")
- **Falar:** *"A procedure aceita a proposta, recusa as outras, muda o projeto para EM_ANDAMENTO e cria o contrato automaticamente."*

---

### Passo 5 — Registrar pagamento

**Opção 12** — Registrar pagamento

- ID do contrato: `1`
- Valor: `1000`
- **Falar:** *"Registro um pagamento no contrato. O trigger do banco impede pagar mais que o valor total."*

---

### Passo 6 — Mostrar total pago (FUNCTION)

**Opção 13** — Calcular total pago

- ID do contrato: `1`
- Resultado esperado: `R$ 1000.00`
- **Falar:** *"A function soma os pagamentos PAGOS e retorna o total."*

---

### Passo 7 (opcional) — Mostrar TRIGGER

Escolha **uma** das duas:

**A) Trigger de proposta** — tentar proposta em projeto fechado

- **Opção 9** — cadastrar proposta no projeto `1` (agora `EM_ANDAMENTO`)
- Deve dar erro: *"Projeto nao esta aberto para receber propostas"*
- **Falar:** *"O trigger valida antes de salvar — o Java só fez INSERT, o banco bloqueou."*

**B) Trigger de pagamento** — tentar pagar acima do contrato

- **Opção 12** — contrato `1`, valor `5000` (contrato é ~2800)
- Deve dar erro de valor ultrapassado
- **Falar:** *"Outro trigger protege o limite do contrato."*

---

### Encerrar

**Opção 0** — Sair

**Frase de fechamento:** *"O sistema usa JDBC no Java para CRUD, JOIN, view, function e procedure; as regras críticas ficam nos triggers do PostgreSQL."*

---

### Checklist rápido (só o essencial)

| # | Opção | O que demonstra |
|---|-------|-----------------|
| 1 | 6 | JOIN (projetos + clientes) |
| 2 | 10 | JOIN (propostas + projetos + freelancers) |
| 3 | 14 | VIEW |
| 4 | 11 | PROCEDURE (aceitar proposta) |
| 5 | 12 | INSERT pagamento |
| 6 | 13 | FUNCTION (total pago) |
| 7 | 9 ou 12 | TRIGGER (opcional, mostrar erro) |
