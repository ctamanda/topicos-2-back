-- V10: usuario administrador inicial
--
-- O /auth/register sempre cria USER; sem esta carga nao existe ADMIN no sistema.
-- Login: admin@fincontrol.com / admin123  (senha em BCrypt, custo 10)
-- Troque a senha antes de qualquer deploy fora do ambiente academico.

INSERT INTO usuario (email, nome, password, role, perfil_fiscal, datacadastro)
VALUES ('admin@fincontrol.com',
        'Administrador',
        '$2a$10$AATTHEmKOQV03MWIHi/IF.5lZaXAQYslPxX/l2l894C7uoHs3nslS',
        'ADMIN',
        'PF',
        NOW());

-- Mesmas 6 categorias que o UsuarioService.criarCategoriasIniciais gera para todo usuario,
-- para o admin tambem conseguir lancar as proprias transacoes.
INSERT INTO categoria (nome, tipo, cor, usuario_id, dataCadastro)
SELECT c.nome, c.tipo, c.cor, u.id, NOW()
FROM usuario u
CROSS JOIN (VALUES
    ('Alimentação', 'DESPESA', '#FF5733'),
    ('Moradia',     'DESPESA', '#33FF57'),
    ('Transporte',  'DESPESA', '#3357FF'),
    ('Lazer',       'DESPESA', '#FF33A1'),
    ('Salário',     'RECEITA', '#28A745'),
    ('Vendas',      'RECEITA', '#FFC107')
) AS c(nome, tipo, cor)
WHERE u.email = 'admin@fincontrol.com';
