package br.unitins.tp2.fincontrol.resource;

import java.time.Duration;
import java.util.Set;

import br.unitins.tp2.fincontrol.dto.LoginRequestDTO;
import br.unitins.tp2.fincontrol.dto.LoginResponseDTO;
import br.unitins.tp2.fincontrol.dto.RegisterRequestDTO;
import br.unitins.tp2.fincontrol.mapper.UsuarioResponseMapper;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.service.UsuarioService;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    private static final long EXPIRES_IN_SECONDS = 3600;

    @Inject
    UsuarioService usuarioService;

    // 201 com os dados públicos do usuário criado (sem senha).
    @POST
    @Path("register")
    public Response register(
            @Valid @NotNull(message = "O corpo da requisição deve ser informado.") RegisterRequestDTO dto) {
        Usuario usuario = usuarioService.criar(
                dto.email(),
                dto.nome(),
                dto.password(),
                dto.perfilFiscal(),
                dto.atividadeMei());

        return Response.status(Response.Status.CREATED)
                .entity(UsuarioResponseMapper.toResponse(usuario))
                .build();
    }

    @POST
    @Path("login")
    public LoginResponseDTO login(
            @Valid @NotNull(message = "O corpo da requisição deve ser informado.") LoginRequestDTO dto) {
        Usuario usuario = usuarioService.autenticar(
                dto.email(),
                dto.password());

        String token = Jwt.issuer("fincontrol")
                .upn(usuario.getEmail())
                .groups(Set.of(usuario.getRole().name()))
                .expiresIn(Duration.ofSeconds(EXPIRES_IN_SECONDS))
                .sign();

        return new LoginResponseDTO(token, EXPIRES_IN_SECONDS);
    }
}
