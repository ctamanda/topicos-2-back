package br.unitins.tp2.fincontrol.service;

import java.util.List;

import br.unitins.tp2.fincontrol.dto.TransacaoRequestDTO;
import br.unitins.tp2.fincontrol.model.Transacao;
import jakarta.validation.Valid;

public interface TransacaoService {

    Transacao create(@Valid TransacaoRequestDTO transacao);
    void update(long id, @Valid TransacaoRequestDTO transacao);
    void delete(long id);
    Transacao findById(long id);
    List<Transacao> findAll(int page, int pageSize);
    List<Transacao> findByDescricao(String descricao, int page, int pageSize);
    long count();
    long count(String descricao);
}
