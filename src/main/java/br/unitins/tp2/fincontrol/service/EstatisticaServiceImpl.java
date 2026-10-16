package br.unitins.tp2.fincontrol.service;

import br.unitins.tp2.fincontrol.dto.EstatisticaResponseDTO;
import br.unitins.tp2.fincontrol.model.Role;
import br.unitins.tp2.fincontrol.repository.CategoriaRepository;
import br.unitins.tp2.fincontrol.repository.TransacaoRepository;
import br.unitins.tp2.fincontrol.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EstatisticaServiceImpl implements EstatisticaService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    TransacaoRepository transacaoRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @Override
    public EstatisticaResponseDTO obterEstatisticas() {
        long totalUsuarios = usuarioRepository.count();
        long totalAdmins = usuarioRepository.count("role = ?1", Role.ADMIN);
        long totalUsers = usuarioRepository.count("role = ?1", Role.USER);
        long totalTransacoes = transacaoRepository.count();
        long totalCategorias = categoriaRepository.count();

        double mediaTransacoesPorUsuario = totalUsuarios == 0
                ? 0.0
                : (double) totalTransacoes / totalUsuarios;

        return new EstatisticaResponseDTO(
                totalUsuarios,
                totalAdmins,
                totalUsers,
                totalTransacoes,
                totalCategorias,
                mediaTransacoesPorUsuario
        );
    }
}