# Candidato
Ygor Augusto silva Martins

# FinanceCore — Gestão de Contas e Transferências

Módulo web de gestão financeira interna construído com **Java 11 + JSP + Servlet + JDBC puro**.

---

## Stack Tecnológica

| Camada        | Tecnologia                          |
|---------------|-------------------------------------|
| Linguagem     | Java 11                             |
| Web           | JSP 2.3 + Servlet 4.0               |
| Persistência  | JDBC puro (sem ORM)                 |
| Pool de conexões | HikariCP 5 (somente pool, sem mapeamento) |
| Banco de dados | PostgreSQL 14+                     |
| Build         | Maven 3.8+                          |
| Servidor      | Apache Tomcat 9+                    |

> **Sem** Spring Boot · Quarkus · Hibernate · JPA · qualquer ORM

---

## Arquitetura

```
src/main/java/br/com/financeiro/
├── model/
│   ├── Conta.java              # Entidade de domínio
│   └── Movimentacao.java       # Entidade de domínio
├── dao/
│   ├── ContaDAO.java           # JDBC: SQL com PreparedStatement
│   └── MovimentacaoDAO.java    # JDBC: SQL com PreparedStatement
├── service/
│   ├── ContaService.java       # Regras de negócio de contas
│   ├── TransferenciaService.java  # Transação JDBC com commit/rollback
│   ├── MovimentacaoService.java
│   └── RegraNegocioException.java
├── servlet/
│   ├── DashboardServlet.java   # GET /  e /dashboard
│   ├── ContaServlet.java       # GET/POST /contas/*
│   ├── TransferenciaServlet.java # GET/POST /transferencias/
│   └── MovimentacaoServlet.java  # GET /movimentacoes/*
└── util/
    ├── ConnectionFactory.java  # HikariCP pool
    └── AppContextListener.java # Lifecycle do pool
```

### Fluxo de uma Transferência (transação JDBC)

```
TransferenciaServlet.doPost()
  └─ TransferenciaService.transferir()
       ├─ conn = ConnectionFactory.getConnection()   // autoCommit=false
       ├─ buscarContaAtiva(origemId)                  // SELECT
       ├─ buscarContaAtiva(destinoId)                 // SELECT
       ├─ validar saldo suficiente
       ├─ contaDAO.atualizarSaldo(conn, origemId, ...)  // UPDATE
       ├─ contaDAO.atualizarSaldo(conn, destinoId, ...) // UPDATE
       ├─ movDAO.inserir(conn, movSaida)               // INSERT
       ├─ movDAO.inserir(conn, movEntrada)             // INSERT
       ├─ conn.commit()   ← explícito em caso de sucesso
       └─ conn.rollback() ← explícito em qualquer exceção
```

---

## Regras de Negócio

- Número de conta deve ser único
- Saldo inicial não pode ser negativo
- Conta inativa não recebe nem envia transferências
- Conta com saldo ≠ 0 não pode ser desativada
- Valor de transferência deve ser > 0
- Origem e destino não podem ser a mesma conta
- Saldo insuficiente bloqueia a transferência

---

## Segurança de SQL

**Todas as queries usam `PreparedStatement` com `?` como placeholder.**
Nenhum dado de entrada do usuário é concatenado em SQL.

```java
// CORRETO — neste projeto
String sql = "SELECT * FROM conta WHERE numero = ?";
ps.setString(1, numero);

// NUNCA FEITO — SQL injection
String sql = "SELECT * FROM conta WHERE numero = '" + numero + "'"; // ❌
```

---

## Configuração e Execução

### 1. Pré-requisitos

- Java 11+
- Maven 3.8+
- PostgreSQL 14+
- Tomcat 9+ (ou use o plugin Maven embutido)

### 2. Banco de dados

```bash
# Criar banco
psql -U postgres -c "CREATE DATABASE financeiro;"

# Executar schema
psql -U postgres -d financeiro -f sql/schema.sql
```

### 3. Variáveis de ambiente (opcional)

```bash
export DB_URL=jdbc:postgresql://localhost:5432/financeiro
export DB_USER=postgres
export DB_PASS=postgres
```

Se não definidas, os valores acima são usados como padrão.

### 4. Build e execução

```bash
# Build
mvn clean package

# Deploy no Tomcat embutido (dev)
mvn tomcat7:run

# Ou deploy manual: copie target/gestao-financeira.war para webapps/ do Tomcat
```

### 5. Acesso

```
http://localhost:8080/financeiro/
```

---

## Funcionalidades

| Módulo           | URL                              | Descrição                         |
|------------------|----------------------------------|-----------------------------------|
| Painel           | `/`                              | Resumo geral e últimas movimentos |
| Listar contas    | `/contas/`                       | Todas as contas com status        |
| Nova conta       | `/contas/nova`                   | Formulário de cadastro            |
| Editar conta     | `/contas/editar/{id}`            | Editar titular, tipo, status      |
| Transferência    | `/transferencias/`               | Formulário de transferência       |
| Extrato          | `/movimentacoes/extrato?contaId` | Histórico por conta               |
| Movimentações    | `/movimentacoes/`                | Últimas 20 movimentações globais  |

---

## Estrutura de Diretórios Completa

```
gestao-financeira/
├── pom.xml
├── sql/
│   └── schema.sql
└── src/main/
    ├── java/br/com/financeiro/
    │   ├── model/          Conta · Movimentacao
    │   ├── dao/            ContaDAO · MovimentacaoDAO
    │   ├── service/        ContaService · TransferenciaService · MovimentacaoService
    │   ├── servlet/        DashboardServlet · ContaServlet · TransferenciaServlet · MovimentacaoServlet
    │   └── util/           ConnectionFactory · AppContextListener
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml
        │   └── layout.jsp  (template de cabeçalho)
        ├── css/app.css
        └── views/
            ├── dashboard.jsp
            ├── contas/       lista.jsp · form.jsp
            ├── transferencias/ form.jsp
            ├── movimentacoes/  extrato.jsp · recentes.jsp
            └── error/          404.jsp · 500.jsp
```