package br.unitins.tp2.fincontrol.service;

import java.util.List;

import br.unitins.tp2.fincontrol.model.AtividadeMei;
import br.unitins.tp2.fincontrol.model.Categoria;
import br.unitins.tp2.fincontrol.model.PerfilFiscal;
import br.unitins.tp2.fincontrol.model.Role;
import br.unitins.tp2.fincontrol.model.TipoCategoria;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository repository;

    @Inject
    EntityManager entityManager;

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

        criarCategoriasIniciais(usuario);

        return usuario;
    }

    private void criarCategoriasIniciais(Usuario usuario) {
        List<Categoria> categorias = List.of(
                novaCategoria("Alimentação", TipoCategoria.DESPESA, "#FF5733", usuario),
                novaCategoria("Moradia", TipoCategoria.DESPESA, "#33FF57", usuario),
                novaCategoria("Transporte", TipoCategoria.DESPESA, "#3357FF", usuario),
                novaCategoria("Lazer", TipoCategoria.DESPESA, "#FF33A1", usuario),
                novaCategoria("Salário", TipoCategoria.RECEITA, "#28A745", usuario),
                novaCategoria("Vendas", TipoCategoria.RECEITA, "#FFC107", usuario));

        for (Categoria c : categorias) {
            entityManager.persist(c);
        }
    }

    private Categoria novaCategoria(String nome, TipoCategoria tipo, String cor, Usuario usuario) {
        Categoria c = new Categoria();
        c.setNome(nome);
        c.setTipo(tipo);
        c.setCor(cor);
        c.setUsuario(usuario);
        return c;
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