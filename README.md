# Freela Facil

Trabalho final da disciplina de Banco de Dados.

## Descricao do sistema

**Nome:** Freela Facil

**Area de atuacao:** plataforma de freelancers

**Objetivo:** permitir o cadastro de clientes, freelancers, projetos, propostas, contratos e pagamentos.

**Problema que resolve:** centraliza a publicacao de projetos e a contratacao de freelancers, deixando mais simples acompanhar propostas, contratos e valores pagos.

## Regras de negocio

1. Um projeto so pode receber propostas enquanto estiver com status `ABERTO`.
2. Ao aceitar uma proposta, o sistema cria um contrato e altera o projeto para `EM_ANDAMENTO`.
3. O pagamento total de um contrato nao pode ultrapassar o valor combinado no contrato.

## Entidades

- `clientes`: pessoas ou empresas que publicam projetos.
- `freelancers`: profissionais que enviam propostas.
- `projetos`: demandas publicadas por clientes.
- `propostas`: ofertas enviadas por freelancers para projetos.
- `contratos`: contrato gerado a partir de uma proposta aceita.
- `pagamentos`: pagamentos feitos em um contrato.

## Relacionamentos

- Um cliente pode ter varios projetos.
- Um projeto pertence a um cliente.
- Um projeto pode receber varias propostas.
- Uma proposta pertence a um projeto e a um freelancer.
- Uma proposta aceita pode gerar um contrato.
- Um contrato pode ter varios pagamentos.

## Diagrama ER

O diagrama esta em [docs/diagrama-er.svg](docs/diagrama-er.svg).

## Scripts SQL

- [database/01_ddl.sql](database/01_ddl.sql): criacao das tabelas, constraints, view, function e procedure.
- [database/02_dml.sql](database/02_dml.sql): inserts, updates, deletes e consultas com join.

## Recursos obrigatorios usados

- Minimo de 5 entidades: o projeto possui 6 entidades.
- Modelo ER: disponivel em SVG.
- DDL e DML: scripts separados na pasta `database`.
- CRUD completo: implementado em Java com JDBC.
- JOIN: existem consultas no SQL e no Java.
- View: `vw_resumo_projetos`.
- Function: `fn_total_pago_contrato`.
- Procedure: `sp_aceitar_proposta`.

## Visao geral do projeto

Este e um trabalho de faculdade de Banco de Dados com duas partes:

1. **Banco PostgreSQL** — tabelas, relacionamentos, view, function, procedure e triggers (`database/`).
2. **Aplicacao Java (JDBC)** — menu no terminal para CRUD e consultas (`src/`).

Fluxo tipico:

```
config/database.properties  →  Conexao.java  →  DAOs  →  Main.java (menu)
```

| Pasta / arquivo | Funcao |
|-----------------|--------|
| `database/01_ddl.sql` | Cria tabelas, constraints, view, function, procedure |
| `database/02_dml.sql` | Insere dados de exemplo e consultas |
| `src/Conexao.java` | Abre conexao JDBC com o PostgreSQL |
| `src/*DAO.java` | Comandos SQL (INSERT, SELECT, UPDATE, DELETE) |
| `src/Main.java` | Menu interativo no terminal |
| `docs/modelo-dados.html` | Documentacao visual do modelo ER |

## Conexao com o banco (Railway)

O JDBC le a configuracao nesta ordem:

1. Variavel de ambiente `DATABASE_URL` (formato `postgresql://usuario:senha@host:porta/banco`)
2. Arquivo `config/database.properties` (nao vai para o Git)

### Configurar

```powershell
cd freela-facil-db
copy config\database.properties.example config\database.properties
```

Edite `config/database.properties` com os dados do Railway (Postgres → Connect):

```properties
jdbc.url=jdbc:postgresql://HOST:PORTA/railway?sslmode=require
jdbc.user=postgres
jdbc.password=SUA_SENHA
```

Ou defina a URL completa:

```powershell
$env:DATABASE_URL = "postgresql://postgres:SENHA@HOST:PORTA/railway"
```

## Como rodar (Windows / PowerShell)

### 1. Baixar o driver JDBC

Baixe o `.jar` do PostgreSQL e coloque em `lib/`:

https://jdbc.postgresql.org/download/

Exemplo: `lib/postgresql-42.7.4.jar`

### 2. Compilar

```powershell
javac -encoding UTF-8 -cp "lib/postgresql-42.7.4.jar" -d out src/*.java
```

### 3. Criar tabelas e dados no Railway (primeira vez)

```powershell
java -cp "out;lib/postgresql-42.7.4.jar" SetupDatabase
```

Isso executa `01_ddl.sql` e `02_dml.sql` no banco configurado.

### 4. Testar conexao (opcional)

```powershell
java -cp "out;lib/postgresql-42.7.4.jar" TesteConexao
```

### 5. Rodar o sistema

```powershell
java -cp "out;lib/postgresql-42.7.4.jar" Main
```

No Linux/macOS troque `;` por `:` no `-cp`.

## Menu do sistema

Ao rodar `Main`, aparece um menu com:

- **1–4** — CRUD de clientes
- **5–8** — CRUD de projetos
- **9–10** — Propostas (com JOIN)
- **11** — Aceitar proposta (`sp_aceitar_proposta`)
- **12–13** — Pagamentos e total pago (`fn_total_pago_contrato`)
- **14** — View `vw_resumo_projetos`
- **0** — Sair

## Alternativa: criar banco com psql

Se tiver o `psql` instalado:

```bash
psql "postgresql://usuario:senha@host:porta/railway" -f database/01_ddl.sql
psql "postgresql://usuario:senha@host:porta/railway" -f database/02_dml.sql
```

## O que apresentar

1. Explicar o problema do sistema.
2. Mostrar o diagrama ER.
3. Mostrar as tabelas e relacionamentos.
4. Executar exemplos de CRUD no Java.
5. Mostrar a consulta com JOIN.
6. Mostrar a view `vw_resumo_projetos`.
7. Mostrar a function `fn_total_pago_contrato`.
8. Mostrar a procedure `sp_aceitar_proposta`.

## Conclusao

O trabalho ajuda a praticar modelagem relacional, comandos SQL, relacionamento entre tabelas e integracao com Java usando JDBC. A maior dificuldade fica em conectar corretamente o Java ao banco e organizar os comandos SQL com `PreparedStatement`.

Possiveis melhorias seriam criar uma interface grafica, autenticar usuarios, adicionar categorias de projetos e melhorar o controle de pagamentos.
