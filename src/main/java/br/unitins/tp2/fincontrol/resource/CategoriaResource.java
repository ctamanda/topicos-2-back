package br.unitins.tp2.fincontrol.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import br.unitins.tp2.fincontrol.dto.CategoriaRequestDTO;
import br.unitins.tp2.fincontrol.dto.CategoriaResponseDTO;
import br.unitins.tp2.fincontrol.mapper.CategoriaResponseMapper;
import br.unitins.tp2.fincontrol.model.Categoria;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.service.CategoriaService;
import br.unitins.tp2.fincontrol.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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

@Path("categorias")
@RolesAllowed({"ADMIN", "USER"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    @Inject
    CategoriaService service;

    @Inject
    UsuarioService usuarioService;

    @Inject
    JsonWebToken jwt;

    @GET
    public List<CategoriaResponseDTO> listar() {
        return service.findAll(getUsuarioLogado()).stream()
                .map(CategoriaResponseMapper::toResponse)
                .toList();
    }

    @GET
    @Path("/{id}")
    public CategoriaResponseDTO buscarPorId(@PathParam("id") Long id) {
        return CategoriaResponseMapper.toResponse(service.findById(id, getUsuarioLogado()));
    }

    @POST
    public Response incluir(@Valid CategoriaRequestDTO dto) {
        Categoria categoria = service.create(dto, getUsuarioLogado());
        return Response.status(Response.Status.CREATED)
                .entity(CategoriaResponseMapper.toResponse(categoria))
                .build();
    }

    @PUT
    @Path("/{id}")
    public CategoriaResponseDTO alterar(@PathParam("id") Long id,
            @Valid CategoriaRequestDTO dto) {
        return CategoriaResponseMapper.toResponse(service.update(id, dto, getUsuarioLogado()));
    }

    @DELETE
    @Path("/{id}")
    public Response apagar(@PathParam("id") Long id) {
        service.delete(id, getUsuarioLogado());
        return Response.noContent().build();
    }

    private Usuario getUsuarioLogado() {
        String email = jwt.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            throw new IllegalStateException(
                    "Usuario autenticado nao encontrado no banco: " + email);
        }
        return usuario;
    }
}