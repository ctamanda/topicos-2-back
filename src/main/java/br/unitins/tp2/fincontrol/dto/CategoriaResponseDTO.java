package br.unitins.tp2.fincontrol.dto;

import br.unitins.tp2.fincontrol.model.TipoCategoria;

public record CategoriaResponseDTO(
    Long id,
    String nome,
    TipoCategoria tipo,
    String cor
) {
}