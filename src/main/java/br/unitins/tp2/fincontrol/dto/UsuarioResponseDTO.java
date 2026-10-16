package br.unitins.tp2.fincontrol.dto;

import br.unitins.tp2.fincontrol.model.AtividadeMei;
import br.unitins.tp2.fincontrol.model.PerfilFiscal;
import br.unitins.tp2.fincontrol.model.Role;

public record UsuarioResponseDTO(
        Long id,
        String email,
        String nome,
        PerfilFiscal perfilFiscal,
        AtividadeMei atividadeMei,
        Role role
) {
}
