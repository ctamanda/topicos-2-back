package br.unitins.tp2.fincontrol.repository;

import br.unitins.tp2.fincontrol.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    // E-mail não diferencia maiúsculas: "Maria@x.com" e "maria@x.com" são a mesma conta.
    public Usuario findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return find("lower(email) = ?1", email.trim().toLowerCase()).firstResult();
    }
}
