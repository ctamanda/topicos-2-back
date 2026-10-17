package br.unitins.tp2.fincontrol.service;

import java.util.List;

import br.unitins.tp2.fincontrol.dto.CategoriaRequestDTO;
import br.unitins.tp2.fincontrol.exception.ResourceNotFoundException;
import br.unitins.tp2.fincontrol.exception.ValidationException;
import br.unitins.tp2.fincontrol.model.Categoria;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.repository.CategoriaRepository;
import br.unitins.tp2.fincontrol.repository.TransacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CategoriaServiceImpl implements CategoriaService {

    @Inject
    CategoriaRepository repository;

    @Inject
    TransacaoRepository transacaoRepository;

    @Override
    @Transactional
    public Categoria create(CategoriaRequestDTO dto, Usuario usuario) {
        Categoria categoria = new Categoria();
        categoria.setUsuario(usuario);
        aplicarDados(categoria, dto, usuario);

        repository.persist(categoria);

        return categoria;
    }

    @Override
    @Transactional
    public Categoria update(Long id, CategoriaRequestDTO dto, Usuario usuario) {
        Categoria categoria = buscarCategoriaOuFalhar(id, usuario);
        aplicarDados(categoria, dto, usuario);
        return categoria;
    }

    @Override
    @Transactional
    public void delete(Long id, Usuario usuario) {
        Categoria categoria = buscarCategoriaOuFalhar(id, usuario);

        // Sem esta checagem a FK de transacao estoura como erro 500.
        if (transacaoRepository.count("categoria.id", categoria.getId()) > 0) {
            throw ValidationException.of("categoria",
                    "Esta categoria possui transações e não pode ser excluída.");
        }

        repository.delete(categoria);
    }

    @Override
    public Categoria findById(Long id, Usuario usuario) {
        return buscarCategoriaOuFalhar(id, usuario);
    }

    @Override
    public List<Categoria> findAll(Usuario usuario) {
        return repository.findByUsuario(usuario.getId());
    }

    private void aplicarDados(Categoria categoria, CategoriaRequestDTO dto, Usuario usuario) {
        Categoria existente = repository.findByUsuarioAndNome(usuario.getId(), dto.nome());
        if (existente != null && !existente.getId().equals(categoria.getId())) {
            throw ValidationException.of("nome",
                    "Ja existe uma categoria com esse nome.");
        }

        categoria.setNome(dto.nome());
        categoria.setTipo(dto.tipo());
        categoria.setCor(dto.cor());
    }

    private Categoria buscarCategoriaOuFalhar(Long id, Usuario usuario) {
        Categoria categoria = repository.findByUsuarioAndId(usuario.getId(), id);

        if (categoria == null)
            throw new ResourceNotFoundException("Categoria nao encontrada.");

        return categoria;
    }
}