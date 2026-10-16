-- Adiciona coluna de dono na transação.
-- Estratégia: adiciona nullable, cria FK, limpa órfãs, depois NOT NULL.

ALTER TABLE transacao ADD COLUMN usuario_id BIGINT;

ALTER TABLE transacao ADD CONSTRAINT fk_transacao_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id);

CREATE INDEX idx_transacao_usuario ON transacao(usuario_id);

-- Deletar transações antigas (decisão 1A)
DELETE FROM transacao WHERE usuario_id IS NULL;

-- Tornar obrigatória
ALTER TABLE transacao ALTER COLUMN usuario_id SET NOT NULL;