CREATE TABLE parametro_fiscal (
    id BIGSERIAL PRIMARY KEY,
    chave VARCHAR(50) NOT NULL,
    valor NUMERIC(15, 2) NOT NULL,
    vigencia_inicio DATE NOT NULL,
    vigencia_fim DATE NOT NULL,
    datacadastro TIMESTAMP,
    dataalteracao TIMESTAMP,

    CONSTRAINT uk_parametro_fiscal_chave_vigencia
        UNIQUE (chave, vigencia_inicio),

    CONSTRAINT ck_parametro_fiscal_vigencia
        CHECK (vigencia_fim >= vigencia_inicio)
);