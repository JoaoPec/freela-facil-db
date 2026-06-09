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

## Como criar o banco

1. Crie um banco PostgreSQL chamado `freela_facil`.
2. Execute o script:

```bash
psql -U postgres -d freela_facil -f database/01_ddl.sql
psql -U postgres -d freela_facil -f database/02_dml.sql
```

## Como rodar o Java

Baixe o driver JDBC do PostgreSQL e coloque o arquivo `.jar` dentro da pasta `lib`.

Link: https://jdbc.postgresql.org/download/

Compile:

```bash
javac -cp "lib/postgresql-42.7.4.jar" -d out src/*.java
```

Execute:

```bash
java -cp "out;lib/postgresql-42.7.4.jar" Main
```

Se o nome do `.jar` baixado for diferente, ajuste o comando.

## Configuracao da conexao

O arquivo [src/Conexao.java](src/Conexao.java) usa os seguintes dados:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/freela_facil";
private static final String USUARIO = "postgres";
private static final String SENHA = "postgres";
```

Altere usuario e senha se o seu PostgreSQL estiver diferente.

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
