-- Adiciona coluna de dono na categoria.
-- Estrategia: adiciona nullable, cria FK, limpa orfas, depois NOT NULL.

ALTER TABLE categoria ADD COLUMN usuario_id BIGINT;

ALTER TABLE categoria ADD CONSTRAINT fk_categoria_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id);

CREATE INDEX idx_categoria_usuario ON categoria(usuario_id);

-- Remove categorias orfas (a "Alimentacao" antiga sem dono).
-- Confirmado que nao ha transacoes referenciando (COUNT = 0).
DELETE FROM categoria WHERE usuario_id IS NULL;

ALTER TABLE categoria ALTER COLUMN usuario_id SET NOT NULL;

-- Nome unico por usuario: o mesmo usuario nao pode ter duas "Alimentacao"
ALTER TABLE categoria ADD CONSTRAINT uk_categoria_nome_usuario
    UNIQUE (nome, usuario_id);