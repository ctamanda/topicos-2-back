package br.unitins.tp2.fincontrol.dto;

public record EstatisticaResponseDTO(
        long totalUsuarios,
        long totalAdmins,
        long totalUsers,
        long totalTransacoes,
        long totalCategorias,
        double mediaTransacoesPorUsuario
) {
}