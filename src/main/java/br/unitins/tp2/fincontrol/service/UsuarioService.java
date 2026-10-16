package br.unitins.tp2.fincontrol.service;

import br.unitins.tp2.fincontrol.model.AtividadeMei;
import br.unitins.tp2.fincontrol.model.PerfilFiscal;
import br.unitins.tp2.fincontrol.model.Role;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository repository;

    @Transactional
    public Usuario criar(
            String email,
            String nome,
            String password,
            PerfilFiscal perfilFiscal,
            AtividadeMei atividadeMei) {

        if (repository.findByEmail(email) != null) {
            throw new BadRequestException("E-mail já cadastrado");
        }

        if (perfilFiscal == null) {
            throw new BadRequestException("O perfil fiscal deve ser informado");
        }

        if (perfilFiscal == PerfilFiscal.MEI && atividadeMei == null) {
            throw new BadRequestException(
                    "A atividade MEI deve ser informada para o perfil fiscal MEI");
        }

        if (perfilFiscal != PerfilFiscal.MEI) {
            atividadeMei = null;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setPassword(BcryptUtil.bcryptHash(password));
        usuario.setPerfilFiscal(perfilFiscal);
        usuario.setAtividadeMei(atividadeMei);

        usuario.setRole(Role.USER);

        repository.persist(usuario);

        return usuario;
    }

    public Usuario autenticar(String email, String password) {
        Usuario usuario = repository.findByEmail(email);

        if (usuario == null
                || !BcryptUtil.matches(password, usuario.getPassword())) {
            throw new NotAuthorizedException("Credenciais inválidas");
        }

        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }
}