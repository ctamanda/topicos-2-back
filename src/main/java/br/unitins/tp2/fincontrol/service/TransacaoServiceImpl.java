package br.unitins.tp2.fincontrol.service;

import java.util.ArrayList;
import java.util.List;

import br.unitins.tp2.fincontrol.dto.TransacaoRequestDTO;
import br.unitins.tp2.fincontrol.exception.Problem;
import br.unitins.tp2.fincontrol.exception.ResourceNotFoundException;
import br.unitins.tp2.fincontrol.exception.ValidationException;
import br.unitins.tp2.fincontrol.model.Categoria;
import br.unitins.tp2.fincontrol.model.Transacao;
import br.unitins.tp2.fincontrol.repository.CategoriaRepository;
import br.unitins.tp2.fincontrol.repository.TransacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TransacaoServiceImpl implements TransacaoService {

    private static final int PAGE_SIZE_MAX = 100;

    @Inject
    TransacaoRepository transacaoRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public Transacao create(TransacaoRequestDTO transacao) {
        Transacao novaTransacao = new Transacao();
        aplicarDados(novaTransacao, transacao);

        transacaoRepository.persist(novaTransacao);

        return novaTransacao;
    }

    @Override
    @Transactional
    public void update(long id, TransacaoRequestDTO transacao) {
        Transacao edicaoTransacao = buscarTransacaoOuFalhar(id);
        aplicarDados(edicaoTransacao, transacao);
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!transacaoRepository.deleteById(id))
            throw new ResourceNotFoundException("Transação não encontrada.");
    }

    @Override
    public Transacao findById(long id) {
        return buscarTransacaoOuFalhar(id);
    }

    @Override
    public List<Transacao> findAll(int page, int pageSize) {
        validarPaginacao(page, pageSize);
        return transacaoRepository.findAll().page(page, pageSize).list();
    }

    @Override
    public List<Transacao> findByDescricao(String descricao, int page, int pageSize) {
        validarPaginacao(page, pageSize);
        return transacaoRepository.findByDescricao(descricao).page(page, pageSize).list();
    }

    @Override
    public long count() {
        return transacaoRepository.findAll().count();
    }

    @Override
    public long count(String descricao) {
        return transacaoRepository.findByDescricao(descricao).count();
    }

    private void validarPaginacao(int page, int pageSize) {
        List<Problem.FieldError> erros = new ArrayList<>();

        if (page < 0)
            erros.add(new Problem.FieldError("page", "A página deve ser maior ou igual a 0."));
        if (pageSize < 1 || pageSize > PAGE_SIZE_MAX)
            erros.add(new Problem.FieldError("pageSize", "O tamanho da página deve estar entre 1 e " + PAGE_SIZE_MAX + "."));

        if (!erros.isEmpty())
            throw new ValidationException("Parâmetros de paginação inválidos", erros);
    }

    private void aplicarDados(Transacao destino, TransacaoRequestDTO origem) {
        destino.setDescricao(origem.descricao());
        destino.setValor(origem.valor());
        destino.setData(origem.data());
        destino.setTipo(origem.tipo());
        destino.setEscopo(origem.escopo());
        destino.setCategoria(buscarCategoriaOuFalhar(origem.idCategoria()));
    }

    private Categoria buscarCategoriaOuFalhar(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria);
        if (categoria == null)
            throw ValidationException.of("idCategoria", "Categoria não encontrada.");

        return categoria;
    }

    private Transacao buscarTransacaoOuFalhar(long id) {
        Transacao transacao = transacaoRepository.findById(id);
        if (transacao == null)
            throw new ResourceNotFoundException("Transação não encontrada.");

        return transacao;
    }
}
