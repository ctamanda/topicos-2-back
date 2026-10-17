package br.unitins.tp2.fincontrol.exception;

import java.time.OffsetDateTime;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

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
        // JSON malformado chega embrulhado num 400 genérico: explica o problema.
        JsonProcessingException json = procurarCausa(t, JsonProcessingException.class);
        if (json != null) {
            return problema(Response.Status.BAD_REQUEST, "/errors/validation-error", "Erro de validação",
                    "Um ou mais campos não passaram na validação.", JsonInvalido.erros(json));
        }

        // Constraint do banco (unique/FK) em corrida entre requisições: conflito, não erro interno.
        org.hibernate.exception.ConstraintViolationException constraint =
                procurarCausa(t, org.hibernate.exception.ConstraintViolationException.class);
        if (constraint != null) {
            LOG.warnf("Violação de constraint %s em %s", constraint.getConstraintName(),
                    (uriInfo != null ? uriInfo.getRequestUri() : "<no-uri>"));
            return problema(Response.Status.CONFLICT, "/errors/conflict", "Conflito",
                    "O registro conflita com outro já existente ou está em uso.", null);
        }

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

    private Response problema(Response.Status status, String tipo, String titulo, String detalhe,
            java.util.List<Problem.FieldError> erros) {
        Problem p = new Problem();
        p.type = baseUrl + tipo;
        p.title = titulo;
        p.status = status.getStatusCode();
        p.detail = detalhe;
        p.instance = (uriInfo != null ? uriInfo.getRequestUri().getPath() : null);
        p.timestamp = OffsetDateTime.now();
        p.errors = erros;
        return Response.status(p.status).type("application/problem+json").entity(p).build();
    }

    private static <T extends Throwable> T procurarCausa(Throwable t, Class<T> tipo) {
        for (Throwable atual = t; atual != null; atual = atual.getCause()) {
            if (tipo.isInstance(atual)) {
                return tipo.cast(atual);
            }
            if (atual.getCause() == atual) {
                break;
            }
        }
        return null;
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
