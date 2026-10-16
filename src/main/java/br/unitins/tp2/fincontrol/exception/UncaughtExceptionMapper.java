package br.unitins.tp2.fincontrol.exception;

import java.time.OffsetDateTime;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UncaughtExceptionMapper.class);

    @ConfigProperty(name = "problem.base-url")
    String baseUrl;

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable t) {
        // Erros HTTP "normais" do JAX-RS (404, 405, 415...) mantêm o status original
        // em vez de virarem 500 só porque este mapper captura Throwable.
        if (t instanceof WebApplicationException wae) {
            int status = wae.getResponse().getStatus();
            if (status >= 400 && status < 500) {
                return toClientErrorResponse(status, wae);
            }
        }

        // Log completo do erro (stacktrace) no servidor
        LOG.errorf(t, "Erro inesperado em %s", (uriInfo != null ? uriInfo.getRequestUri() : "<no-uri>"));

        Problem p = new Problem();
        p.type = baseUrl + "/errors/unexpected-error";
        p.title = "Erro inesperado";
        p.status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        p.detail = "Ocorreu um erro inesperado. Tente novamente mais tarde.";
        p.instance = (uriInfo != null ? uriInfo.getRequestUri().getPath() : null);
        p.timestamp = OffsetDateTime.now();
        // se você tiver um traceId de um filtro, injete aqui:
        // p.traceId = MDC.get("traceId"); // exemplo se usar SLF4J/MDC

        return Response.status(p.status)
                .type("application/problem+json")
                .entity(p)
                .build();
    }

    private Response toClientErrorResponse(int status, WebApplicationException e) {
        Response.Status reason = Response.Status.fromStatusCode(status);

        Problem p = new Problem();
        p.type = baseUrl + "/errors/http-error";
        p.title = (reason != null ? reason.getReasonPhrase() : "Erro na requisição");
        p.status = status;
        p.detail = e.getMessage();
        p.instance = (uriInfo != null ? uriInfo.getRequestUri().getPath() : null);
        p.timestamp = OffsetDateTime.now();

        return Response.status(status)
                .type("application/problem+json")
                .entity(p)
                .build();
    }
}
