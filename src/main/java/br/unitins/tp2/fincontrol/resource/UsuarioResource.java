package br.unitins.tp2.fincontrol.resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import br.unitins.tp2.fincontrol.dto.UsuarioResponseDTO;
import br.unitins.tp2.fincontrol.dto.UsuarioUpdateDTO;
import br.unitins.tp2.fincontrol.mapper.UsuarioResponseMapper;
import br.unitins.tp2.fincontrol.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("usuarios")
@RolesAllowed({"ADMIN", "USER"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("me")
    public UsuarioResponseDTO buscarMeuPerfil() {
        return UsuarioResponseMapper.toResponse(usuarioService.buscarLogado(jwt.getName()));
    }

    @PUT
    @Path("me")
    public UsuarioResponseDTO atualizarMeuPerfil(
            @Valid @NotNull(message = "O corpo da requisição deve ser informado.") UsuarioUpdateDTO dto) {
        return UsuarioResponseMapper.toResponse(usuarioService.atualizarPerfil(jwt.getName(), dto));
    }
}
