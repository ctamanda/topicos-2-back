package br.unitins.tp2.fincontrol.service;

import java.util.List;

import br.unitins.tp2.fincontrol.dto.CategoriaRequestDTO;
import br.unitins.tp2.fincontrol.model.Categoria;
import br.unitins.tp2.fincontrol.model.Usuario;

public interface CategoriaService {

    Categoria create(CategoriaRequestDTO dto, Usuario usuario);

    Categoria update(Long id, CategoriaRequestDTO dto, Usuario usuario);

    void delete(Long id, Usuario usuario);

    Categoria findById(Long id, Usuario usuario);

    List<Categoria> findAll(Usuario usuario);
}