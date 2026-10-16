package br.unitins.tp2.fincontrol.service;

import java.time.LocalDate;
import java.util.List;

import br.unitins.tp2.fincontrol.dto.ParametroFiscalRequestDTO;
import br.unitins.tp2.fincontrol.exception.ResourceNotFoundException;
import br.unitins.tp2.fincontrol.model.ParametroFiscal;
import br.unitins.tp2.fincontrol.repository.ParametroFiscalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class ParametroFiscalServiceImpl implements ParametroFiscalService {

    @Inject
    ParametroFiscalRepository repository;

    @Override
    public List<ParametroFiscal> findAll() {
        return repository.find("order by chave asc, vigenciaInicio desc").list();
    }

    @Override
    public ParametroFiscal findById(Long id) {
        ParametroFiscal parametro = repository.findById(id);

        if (parametro == null) {
            throw new ResourceNotFoundException("Parâmetro fiscal não encontrado.");
        }

        return parametro;
    }

    @Override
    @Transactional
    public ParametroFiscal create(ParametroFiscalRequestDTO dto) {
        validarPeriodo(dto);

        verificarSobreposicao(
                dto.chave(),
                dto.vigenciaInicio(),
                dto.vigenciaFim(),
                null
        );

        ParametroFiscal parametro = new ParametroFiscal();
        aplicarDados(parametro, dto);

        repository.persist(parametro);

        return parametro;
    }

    @Override
    @Transactional
    public ParametroFiscal update(Long id, ParametroFiscalRequestDTO dto) {
        validarPeriodo(dto);

        ParametroFiscal parametro = findById(id);

        verificarSobreposicao(
                dto.chave(),
                dto.vigenciaInicio(),
                dto.vigenciaFim(),
                id
        );

        aplicarDados(parametro, dto);

        return parametro;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ParametroFiscal parametro = findById(id);

        repository.delete(parametro);
    }

    private void aplicarDados(ParametroFiscal parametro, ParametroFiscalRequestDTO dto) {
        parametro.setChave(dto.chave());
        parametro.setValor(dto.valor());
        parametro.setVigenciaInicio(dto.vigenciaInicio());
        parametro.setVigenciaFim(dto.vigenciaFim());
    }

    private void validarPeriodo(ParametroFiscalRequestDTO dto) {
        if (dto.vigenciaInicio() == null || dto.vigenciaFim() == null) {
            throw new BadRequestException(
                    "As datas inicial e final da vigência devem ser informadas."
            );
        }

        if (dto.vigenciaFim().isBefore(dto.vigenciaInicio())) {
            throw new BadRequestException(
                    "A data final da vigência deve ser igual ou posterior à data inicial."
            );
        }
    }

    private void verificarSobreposicao(
            String chave,
            LocalDate vigenciaInicio,
            LocalDate vigenciaFim,
            Long idIgnorado
    ) {
        List<ParametroFiscal> conflitantes = repository.find(
                "chave = ?1 and vigenciaInicio <= ?2 and vigenciaFim >= ?3",
                chave,
                vigenciaFim,
                vigenciaInicio
        ).list();

        boolean existeSobreposicao = conflitantes.stream()
                .anyMatch(parametro ->
                        idIgnorado == null
                                || !parametro.getId().equals(idIgnorado)
                );

        if (existeSobreposicao) {
            throw new BadRequestException(
                    "Já existe um parâmetro vigente nesse período para essa chave."
            );
        }
    }
}