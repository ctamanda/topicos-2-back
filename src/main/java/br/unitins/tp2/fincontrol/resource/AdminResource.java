package br.unitins.tp2.fincontrol.resource;

import br.unitins.tp2.fincontrol.dto.EstatisticaResponseDTO;
import br.unitins.tp2.fincontrol.service.EstatisticaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("admin")
@RolesAllowed("ADMIN")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    EstatisticaService estatisticaService;

    @GET
    @Path("estatisticas")
    public EstatisticaResponseDTO obterEstatisticas() {
        return estatisticaService.obterEstatisticas();
    }
}