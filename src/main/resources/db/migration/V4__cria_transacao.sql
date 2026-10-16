-- V4: tabela transacao (Many-to-One com categoria)
--
-- Convencoes (mesmas do DefaultEntity, sem @Column nas colunas herdadas):
--   * id            -> IDENTITY (BIGSERIAL)
--   * dataCadastro / dataAlteracao -> nomes do atributo, sem snake_case
--     (o Quarkus nao converte camelCase por padrao; o Postgres guarda em minusculo)
--   * FK            -> id_<entidade>, igual ao @JoinColumn(name = "id_categoria")
--   * enums         -> VARCHAR (@Enumerated(EnumType.STRING)) + CHECK

-- A FK precisa da tabela categoria. Se ela ja foi criada em V1..V3 este comando
-- nao faz nada; se nao, cria a versao minima que o Categoria.java espera.
CREATE TABLE IF NOT EXISTS categoria (
    id             BIGSERIAL   PRIMARY KEY,
    nome           VARCHAR(60) NOT NULL,
    tipo           VARCHAR(10) NOT NULL,
    cor            VARCHAR(20),
    dataCadastro   TIMESTAMP,
    dataAlteracao  TIMESTAMP,
    CONSTRAINT ck_categoria_tipo CHECK (tipo IN ('RECEITA', 'DESPESA'))
);

CREATE TABLE transacao (
    id             BIGSERIAL     PRIMARY KEY,
    descricao      VARCHAR(150)  NOT NULL,
    valor          NUMERIC(15,2) NOT NULL,
    data           DATE          NOT NULL,
    tipo           VARCHAR(10)   NOT NULL,
    escopo         VARCHAR(10)   NOT NULL,
    id_categoria   BIGINT        NOT NULL,
    dataCadastro   TIMESTAMP,
    dataAlteracao  TIMESTAMP,
    CONSTRAINT fk_transacao_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id),
    CONSTRAINT ck_transacao_valor     CHECK (valor > 0),
    CONSTRAINT ck_transacao_tipo      CHECK (tipo IN ('RECEITA', 'DESPESA')),
    CONSTRAINT ck_transacao_escopo    CHECK (escopo IN ('PESSOAL', 'EMPRESA'))
);

-- FK nao ganha indice automatico no Postgres; ajuda no join e no delete de categoria.
CREATE INDEX idx_transacao_categoria ON transacao (id_categoria);
