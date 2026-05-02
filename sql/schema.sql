-- ============================================================
--  Schema: Gestão de Contas e Transferências Financeiras
--  Banco: PostgreSQL (compatível com MySQL com ajustes mínimos)
-- ============================================================

CREATE TABLE IF NOT EXISTS conta (
    id          SERIAL PRIMARY KEY,
    numero      VARCHAR(20)     NOT NULL UNIQUE,
    titular     VARCHAR(120)    NOT NULL,
    tipo        VARCHAR(20)     NOT NULL CHECK (tipo IN ('CORRENTE','POUPANCA','INVESTIMENTO')),
    saldo       NUMERIC(15,2)   NOT NULL DEFAULT 0.00,
    ativa       BOOLEAN         NOT NULL DEFAULT TRUE,
    criada_em   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movimentacao (
    id              SERIAL PRIMARY KEY,
    conta_id        INTEGER         NOT NULL REFERENCES conta(id),
    tipo            VARCHAR(20)     NOT NULL CHECK (tipo IN ('CREDITO','DEBITO','TRANSFERENCIA_ENTRADA','TRANSFERENCIA_SAIDA')),
    valor           NUMERIC(15,2)   NOT NULL CHECK (valor > 0),
    descricao       VARCHAR(255),
    conta_origem_id INTEGER         REFERENCES conta(id),
    conta_destino_id INTEGER        REFERENCES conta(id),
    realizada_em    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_movimentacao_conta    ON movimentacao(conta_id);
CREATE INDEX IF NOT EXISTS idx_movimentacao_data     ON movimentacao(realizada_em);

-- Dados de exemplo
INSERT INTO conta (numero, titular, tipo, saldo) VALUES
    ('0001-1', 'João da Silva',      'CORRENTE',    5000.00),
    ('0002-3', 'Maria Oliveira',     'POUPANCA',   12500.00),
    ('0003-5', 'Carlos Ferreira',    'CORRENTE',    3200.00),
    ('0004-7', 'Ana Costa',          'INVESTIMENTO',50000.00);
