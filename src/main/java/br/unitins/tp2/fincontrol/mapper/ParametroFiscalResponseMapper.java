package br.unitins.tp2.fincontrol.mapper;

import br.unitins.tp2.fincontrol.dto.ParametroFiscalResponseDTO;
import br.unitins.tp2.fincontrol.model.ParametroFiscal;

public final class ParametroFiscalResponseMapper {

    private ParametroFiscalResponseMapper() {
    }

    public static ParametroFiscalResponseDTO toResponse(ParametroFiscal parametro) {
        return new ParametroFiscalResponseDTO(
                parametro.getId(),
                parametro.getChave(),
                parametro.getValor(),
                parametro.getVigenciaInicio(),
                parametro.getVigenciaFim()
        );
    }
}