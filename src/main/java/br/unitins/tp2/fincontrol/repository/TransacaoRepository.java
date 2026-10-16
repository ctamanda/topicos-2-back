package br.unitins.tp2.fincontrol.repository;

import br.unitins.tp2.fincontrol.model.Transacao;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransacaoRepository implements PanacheRepository<Transacao> {

    public PanacheQuery<Transacao> findByDescricao(String descricao) {
        return find("lower(descricao) like lower(?1) order by data desc, id desc",
                "%" + descricao + "%");
    }

    @Override
    public PanacheQuery<Transacao> findAll() {
        return find("order by data desc, id desc");
    }

    public PanacheQuery<Transacao> findByUsuario(Long usuarioId) {
        return find("usuario.id = ?1 order by data desc, id desc", usuarioId);
    }

    public PanacheQuery<Transacao> findByUsuarioAndDescricao(Long usuarioId, String descricao) {
        return find("usuario.id = ?1 and lower(descricao) like lower(?2) order by data desc, id desc",
                usuarioId, "%" + descricao + "%");
    }
}