package br.unitins.tp2.fincontrol.service;

import java.util.List;

import br.unitins.tp2.fincontrol.dto.ParametroFiscalRequestDTO;
import br.unitins.tp2.fincontrol.model.ParametroFiscal;

public interface ParametroFiscalService {

    List<ParametroFiscal> findAll();

    ParametroFiscal findById(Long id);

    ParametroFiscal create(ParametroFiscalRequestDTO dto);

    ParametroFiscal update(Long id, ParametroFiscalRequestDTO dto);

    void delete(Long id);
}