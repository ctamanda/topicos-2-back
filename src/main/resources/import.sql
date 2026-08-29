-- Inserir parametros fiscais
INSERT INTO parametrofiscal (chave, valor, vigencia_inicio, vigencia_fim) VALUES ('SALARIO_MINIMO', '1320.00', '2024-01-01', '2024-12-31');
INSERT INTO parametrofiscal (chave, valor, vigencia_inicio, vigencia_fim) VALUES ('TETO_MEI', '81000.00', '2024-01-01', '2024-12-31');
INSERT INTO parametrofiscal (chave, valor, vigencia_inicio, vigencia_fim) VALUES ('ICMS_DAS', '1.00', '2024-01-01', '2024-12-31');
INSERT INTO parametrofiscal (chave, valor, vigencia_inicio, vigencia_fim) VALUES ('ISS_DAS', '5.00', '2024-01-01', '2024-12-31');
INSERT INTO parametrofiscal (chave, valor, vigencia_inicio, vigencia_fim) VALUES ('LUCRO_PRESUMIDO_SERVICO', '32', '2024-01-01', '2024-12-31');
INSERT INTO parametrofiscal (chave, valor, vigencia_inicio, vigencia_fim) VALUES ('LUCRO_PRESUMIDO_COMERCIO', '8', '2024-01-01', '2024-12-31');
INSERT INTO parametrofiscal (chave, valor, vigencia_inicio, vigencia_fim) VALUES ('LUCRO_PRESUMIDO_TRANSPORTE', '16', '2024-01-01', '2024-12-31');

-- Faixas IRPF
INSERT INTO faixairpf (limite_inferior, limite_superior, aliquota, parcela_deduzir, vigencia) VALUES (0.00, 2259.20, 0.0, 0.0, '2024');
INSERT INTO faixairpf (limite_inferior, limite_superior, aliquota, parcela_deduzir, vigencia) VALUES (2259.21, 2826.65, 7.5, 169.44, '2024');
INSERT INTO faixairpf (limite_inferior, limite_superior, aliquota, parcela_deduzir, vigencia) VALUES (2826.66, 3751.05, 15.0, 422.94, '2024');
INSERT INTO faixairpf (limite_inferior, limite_superior, aliquota, parcela_deduzir, vigencia) VALUES (3751.06, 4664.68, 22.5, 865.28, '2024');
INSERT INTO faixairpf (limite_inferior, limite_superior, aliquota, parcela_deduzir, vigencia) VALUES (4664.69, 999999999.99, 27.5, 1274.64, '2024');