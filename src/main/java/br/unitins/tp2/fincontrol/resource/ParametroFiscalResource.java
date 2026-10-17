package br.unitins.tp2.fincontrol.resource;

import java.util.List;

import br.unitins.tp2.fincontrol.dto.ParametroFiscalRequestDTO;
import br.unitins.tp2.fincontrol.dto.ParametroFiscalResponseDTO;
import br.unitins.tp2.fincontrol.mapper.ParametroFiscalResponseMapper;
import br.unitins.tp2.fincontrol.model.ParametroFiscal;
import br.unitins.tp2.fincontrol.service.ParametroFiscalService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("admin/parametros")
@RolesAllowed("ADMIN")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParametroFiscalResource {

    @Inject
    ParametroFiscalService service;

    @GET
    public List<ParametroFiscalResponseDTO> findAll() {
        return service.findAll()
                .stream()
                .map(ParametroFiscalResponseMapper::toResponse)
                .toList();
    }

    @GET
    @Path("{id}")
    public ParametroFiscalResponseDTO findById(@PathParam("id") Long id) {
        return ParametroFiscalResponseMapper.toResponse(service.findById(id));
    }

    @POST
    public Response create(@Valid @NotNull(message = "O corpo da requisição deve ser informado.") ParametroFiscalRequestDTO dto) {
        ParametroFiscal parametro = service.create(dto);
        ParametroFiscalResponseDTO response = ParametroFiscalResponseMapper.toResponse(parametro);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @PUT
    @Path("{id}")
    public ParametroFiscalResponseDTO update(
            @PathParam("id") Long id,
            @Valid @NotNull(message = "O corpo da requisição deve ser informado.") ParametroFiscalRequestDTO dto
    ) {
        return ParametroFiscalResponseMapper.toResponse(service.update(id, dto));
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}