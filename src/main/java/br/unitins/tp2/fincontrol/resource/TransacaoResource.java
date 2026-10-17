package br.unitins.tp2.fincontrol.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import br.unitins.tp2.fincontrol.dto.PageResponse;
import br.unitins.tp2.fincontrol.dto.TransacaoRequestDTO;
import br.unitins.tp2.fincontrol.dto.TransacaoResponseDTO;
import br.unitins.tp2.fincontrol.mapper.TransacaoResponseMapper;
import br.unitins.tp2.fincontrol.model.Transacao;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.service.TransacaoService;
import br.unitins.tp2.fincontrol.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("transacoes")
@RolesAllowed({"ADMIN", "USER"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransacaoResource {

    @Inject
    TransacaoService service;

    @Inject
    UsuarioService usuarioService;

    @Inject
    JsonWebToken jwt;

    @GET
    public PageResponse<TransacaoResponseDTO> buscarTodos(@QueryParam("descricao") String descricao,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        Usuario usuario = getUsuarioLogado();
        boolean filtrar = descricao != null && !descricao.isBlank();

        List<Transacao> transacoes = filtrar
                ? service.findByDescricao(descricao.trim(), page, pageSize, usuario)
                : service.findAll(page, pageSize, usuario);
        long totalItems = filtrar
                ? service.count(descricao.trim(), usuario)
                : service.count(usuario);

        return PageResponse.of(transacoes, page, pageSize, totalItems, TransacaoResponseMapper::toResponse);
    }

    @GET
    @Path("/{id}")
    public TransacaoResponseDTO buscarPorId(@PathParam("id") Long id) {
        return TransacaoResponseMapper.toResponse(service.findById(id, getUsuarioLogado()));
    }

    @POST
    public TransacaoResponseDTO incluir(@Valid TransacaoRequestDTO dto) {
        return TransacaoResponseMapper.toResponse(service.create(dto, getUsuarioLogado()));
    }

    @PUT
    @Path("/{id}")
    public void alterar(@PathParam("id") Long id, @Valid TransacaoRequestDTO transacao) {
        service.update(id, transacao, getUsuarioLogado());
    }

    @DELETE
    @Path("/{id}")
    public void apagar(@PathParam("id") Long id) {
        service.delete(id, getUsuarioLogado());
    }

    private Usuario getUsuarioLogado() {
        // O upn do JWT agora contém o e-mail do usuário autenticado.
        String email = jwt.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            throw new IllegalStateException(
                    "Usuário autenticado não encontrado no banco: " + email);
        }

        return usuario;
    }
}