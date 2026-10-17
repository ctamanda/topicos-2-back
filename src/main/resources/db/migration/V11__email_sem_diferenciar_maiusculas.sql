-- V11: e-mail não diferencia maiúsculas/minúsculas
--
-- Antes era possível cadastrar "Maria@x.com" e "maria@x.com" como contas diferentes.
-- O back passou a gravar e buscar o e-mail em minúsculas; aqui padronizamos o que já
-- existe e garantimos a regra também no banco.
--
-- Se esta migration falhar no índice, há e-mails repetidos ignorando maiúsculas.
-- Encontre com:  SELECT lower(email), count(*) FROM usuario GROUP BY 1 HAVING count(*) > 1;

UPDATE usuario SET email = lower(trim(email));

CREATE UNIQUE INDEX uk_usuario_email_lower ON usuario (lower(email));
