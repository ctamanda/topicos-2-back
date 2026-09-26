package br.unitins.tp2.fincontrol.mapper;

import br.unitins.tp2.fincontrol.dto.CategoriaResponseDTO;
import br.unitins.tp2.fincontrol.model.Categoria;

public final class CategoriaResponseMapper {

    private CategoriaResponseMapper() {
    }

    public static CategoriaResponseDTO toResponse(Categoria categoria) {
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome());
    }
}
