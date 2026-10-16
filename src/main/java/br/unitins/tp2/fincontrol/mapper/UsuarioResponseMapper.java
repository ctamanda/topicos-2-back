package br.unitins.tp2.fincontrol.mapper;

import br.unitins.tp2.fincontrol.dto.UsuarioResponseDTO;
import br.unitins.tp2.fincontrol.model.Usuario;

public final class UsuarioResponseMapper {

    private UsuarioResponseMapper() {
    }

    public static UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getPerfilFiscal(),
                usuario.getAtividadeMei(),
                usuario.getRole()
        );
    }
}