package br.unitins.tp2.fincontrol.resource;

import java.time.Duration;
import java.util.Set;

import br.unitins.tp2.fincontrol.dto.LoginRequestDTO;
import br.unitins.tp2.fincontrol.dto.LoginResponseDTO;
import br.unitins.tp2.fincontrol.dto.RegisterRequestDTO;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.service.UsuarioService;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UsuarioService usuarioService;

    @POST
    @Path("register")
    public Response register(@Valid RegisterRequestDTO dto) {
        Usuario usuario = usuarioService.criar(
                dto.email(),
                dto.nome(),
                dto.password(),
                dto.perfilFiscal(),
                dto.atividadeMei());

        return Response.status(Response.Status.CREATED)
                .entity("Usuário " + usuario.getEmail() + " criado com sucesso")
                .build();
    }

    @POST
    @Path("login")
    public LoginResponseDTO login(@Valid LoginRequestDTO dto) {
        Usuario usuario = usuarioService.autenticar(
                dto.email(),
                dto.password());

        long expiresInSeconds = 3600;

        String token = Jwt.issuer("fincontrol")
                .upn(usuario.getEmail())
                .groups(Set.of(usuario.getRole().name()))
                .expiresIn(Duration.ofSeconds(expiresInSeconds))
                .sign();

        return new LoginResponseDTO(token, expiresInSeconds);
    }
}