ALTER TABLE usuario
    RENAME COLUMN username TO email;

ALTER TABLE usuario
    ALTER COLUMN email TYPE VARCHAR(254);

ALTER TABLE usuario
    ADD COLUMN nome VARCHAR(100) NOT NULL DEFAULT '';

ALTER TABLE usuario
    ALTER COLUMN nome DROP DEFAULT;

ALTER TABLE usuario
    ADD COLUMN perfil_fiscal VARCHAR(10) NOT NULL DEFAULT 'PF';

ALTER TABLE usuario
    ALTER COLUMN perfil_fiscal DROP DEFAULT;

ALTER TABLE usuario
    ADD COLUMN atividade_mei VARCHAR(10);

ALTER TABLE usuario RENAME CONSTRAINT usuario_username_key TO usuario_email_key;