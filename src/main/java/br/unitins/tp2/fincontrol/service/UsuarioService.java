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

        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado");
        }

        if (!BcryptUtil.matches(password, usuario.getPassword())) {
            throw new NotFoundException("Senha inválida");
        }

        return usuario;
    }

    public Usuario buscarPorUsername(String username) {
        return repository.findByUsername(username);
    }
}