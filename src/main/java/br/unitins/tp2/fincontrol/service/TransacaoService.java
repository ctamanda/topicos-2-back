package br.unitins.tp2.fincontrol.service;

import java.util.List;

import br.unitins.tp2.fincontrol.dto.TransacaoRequestDTO;
import br.unitins.tp2.fincontrol.model.Transacao;
import br.unitins.tp2.fincontrol.model.Usuario;
import jakarta.validation.Valid;

public interface TransacaoService {

    Transacao create(@Valid TransacaoRequestDTO transacao, Usuario usuario);
    void update(long id, @Valid TransacaoRequestDTO transacao, Usuario usuario);
    void delete(long id, Usuario usuario);
    Transacao findById(long id, Usuario usuario);
    List<Transacao> findAll(int page, int pageSize, Usuario usuario);
    List<Transacao> findByDescricao(String descricao, int page, int pageSize, Usuario usuario);
    long count(Usuario usuario);
    long count(String descricao, Usuario usuario);
}