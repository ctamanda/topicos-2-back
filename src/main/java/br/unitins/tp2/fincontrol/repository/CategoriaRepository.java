package br.unitins.tp2.fincontrol.repository;

import java.util.List;

import br.unitins.tp2.fincontrol.model.Categoria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoriaRepository implements PanacheRepository<Categoria> {

    public List<Categoria> findByUsuario(Long usuarioId) {
        return find("usuario.id = ?1 order by nome", usuarioId).list();
    }

    public Categoria findByUsuarioAndId(Long usuarioId, Long id) {
        return find("usuario.id = ?1 and id = ?2", usuarioId, id).firstResult();
    }

    public Categoria findByUsuarioAndNome(Long usuarioId, String nome) {
        return find("usuario.id = ?1 and lower(nome) = lower(?2)", usuarioId, nome).firstResult();
    }
}