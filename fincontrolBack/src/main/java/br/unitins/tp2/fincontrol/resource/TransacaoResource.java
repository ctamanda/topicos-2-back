package br.unitins.tp2.fincontrol.resource;

import java.util.List;

import br.unitins.tp2.fincontrol.dto.PageResponse;
import br.unitins.tp2.fincontrol.dto.TransacaoRequestDTO;
import br.unitins.tp2.fincontrol.dto.TransacaoResponseDTO;
import br.unitins.tp2.fincontrol.mapper.TransacaoResponseMapper;
import br.unitins.tp2.fincontrol.model.Transacao;
import br.unitins.tp2.fincontrol.service.TransacaoService;
import jakarta.inject.Inject;
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

@Path("admin/transacoes")  // s
// @RolesAllowed("ADMIN") 
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransacaoResource {

    @Inject
    TransacaoService service;

    @GET
    public PageResponse<TransacaoResponseDTO> buscarTodos(@QueryParam("descricao") String descricao,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        boolean filtrar = descricao != null && !descricao.isBlank();

        List<Transacao> transacoes = filtrar
                ? service.findByDescricao(descricao.trim(), page, pageSize)
                : service.findAll(page, pageSize);
        long totalItems = filtrar ? service.count(descricao.trim()) : service.count();

        return PageResponse.of(transacoes, page, pageSize, totalItems, TransacaoResponseMapper::toResponse);
    }

    @GET
    @Path("/{id}")
    public TransacaoResponseDTO buscarPorId(@PathParam("id") Long id) {
        return TransacaoResponseMapper.toResponse(service.findById(id));
    }

    @POST
    public TransacaoResponseDTO incluir(TransacaoRequestDTO dto) {
        return TransacaoResponseMapper.toResponse(service.create(dto));
    }

    @PUT
    @Path("/{id}")
    public void alterar(@PathParam("id") Long id, TransacaoRequestDTO transacao) {
        service.update(id, transacao);
    }

    @DELETE
    @Path("/{id}")
    public void apagar(@PathParam("id") Long id) {
        service.delete(id);
    }
}
