package br.unitins.tp2.fincontrol.service;

import br.unitins.tp2.fincontrol.model.Role;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotAuthorizedException;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository repository;

    @Transactional
    public Usuario criar(String username, String password, Role role) {
        if (repository.findByUsername(username) != null) {
            throw new BadRequestException("Username já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(BcryptUtil.bcryptHash(password));
        usuario.setRole(role);

        repository.persist(usuario);
        return usuario;
    }

    public Usuario autenticar(String username, String password) {
        Usuario usuario = repository.findByUsername(username);

        // Mensagem única para evitar enumeração de usuários.
        // O || faz curto-circuito: se usuario == null, não executa matches().
        if (usuario == null || !BcryptUtil.matches(password, usuario.getPassword())) {
            throw new NotAuthorizedException("Credenciais inválidas");
        }

        return usuario;
    }

    public Usuario buscarPorUsername(String username) {
        return repository.findByUsername(username);
    }
}