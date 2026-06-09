# Trabalho Final - Banco de Dados

## 1. Descricao do sistema

**Nome do sistema:** Freela Facil

**Area de atuacao:** plataforma de freelancers.

**Objetivo do sistema:** cadastrar clientes, freelancers, projetos, propostas, contratos e pagamentos.

**Problema que resolve:** ajuda a organizar a contratacao de freelancers, permitindo que clientes publiquem projetos e recebam propostas de profissionais.

## 2. Banco de dados

### 2.1 Modelo de dados

O modelo possui 6 entidades:

- `clientes`
- `freelancers`
- `projetos`
- `propostas`
- `contratos`
- `pagamentos`

O diagrama ER esta no arquivo `docs/diagrama-er.svg`.

### Entidades e relacionamentos

Um cliente pode publicar varios projetos. Cada projeto pertence a apenas um cliente.

Um freelancer pode enviar varias propostas. Cada proposta pertence a um projeto e a um freelancer.

Um projeto pode receber varias propostas, mas uma proposta aceita gera apenas um contrato.

Um contrato pode receber varios pagamentos.

## 2.2 Script DDL

O script DDL esta em `database/01_ddl.sql`.

Ele possui:

- `CREATE TABLE`
- chaves primarias
- chaves estrangeiras
- constraints
- view
- procedure
- function
- trigger para validar pagamentos

## 2.3 Script DML

O script DML esta em `database/02_dml.sql`.

Ele possui:

- `INSERT`
- `UPDATE`
- `DELETE`
- `SELECT`
- consultas com `JOIN`

## 3. Sistema em Java com JDBC

O sistema usa JDBC para conectar no PostgreSQL.

A classe `Conexao.java` centraliza a conexao com o banco.

As classes DAO executam os comandos SQL:

- `ClienteDAO.java`
- `ProjetoDAO.java`
- `PropostaDAO.java`
- `PagamentoDAO.java`

A classe `Main.java` possui um menu simples no terminal.

## 3.2 CRUD completo

O CRUD foi implementado principalmente para clientes e projetos.

Clientes:

- cadastrar cliente
- listar clientes
- atualizar email do cliente
- excluir cliente

Projetos:

- cadastrar projeto
- listar projetos com cliente
- atualizar status do projeto
- excluir projeto

Tambem existem cadastros e consultas para propostas e pagamentos.

## 3.3 Uso de view

A view `vw_resumo_projetos` mostra um resumo dos projetos com nome do cliente e quantidade de propostas.

Ela e usada no Java pelo metodo `listarViewResumo`, da classe `ProjetoDAO`.

## 4. Recursos avancados

### Function

A function `fn_total_pago_contrato` recebe o ID de um contrato e calcula o total ja pago.

Ela usa `SUM` nos pagamentos com status `PAGO`.

### Procedure

A procedure `sp_aceitar_proposta` recebe o ID de uma proposta e executa uma acao importante no sistema.

Ela aceita a proposta, recusa as outras propostas do mesmo projeto, altera o projeto para `EM_ANDAMENTO` e cria um contrato.

## Regras de negocio

1. Um projeto so pode receber uma proposta aceita se estiver aberto.
2. Ao aceitar uma proposta, as outras propostas do mesmo projeto sao recusadas.
3. O pagamento total nao pode ultrapassar o valor total do contrato.

## 5. Conclusao

Com esse trabalho foi possivel praticar modelagem de banco de dados, relacionamentos, comandos SQL e integracao entre Java e PostgreSQL usando JDBC.

A principal dificuldade foi organizar as regras de negocio no banco e no Java sem deixar o sistema muito complexo.

Como melhoria futura, o sistema poderia ter uma interface grafica, login de usuarios, categorias de projetos e uma tela para acompanhar contratos.

## 6. Apresentacao

Durante a apresentacao, e possivel mostrar:

1. O diagrama ER.
2. O script DDL criando as tabelas.
3. O script DML inserindo dados.
4. O menu Java rodando no terminal.
5. Uma consulta com JOIN.
6. O uso da view.
7. A procedure aceitando uma proposta.
8. A function calculando total pago.
