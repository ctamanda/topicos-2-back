package br.unitins.tp2.fincontrol.exception;

import java.time.OffsetDateTime;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Substitui a resposta padrão do Quarkus ({"objectName":...}) para valores de tipo errado no JSON,
// como enum inexistente ou data inválida.
@Provider
@ApplicationScoped
public class JsonInvalidoExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    @Context
    UriInfo uri;

    @ConfigProperty(name = "problem.base-url")
    String baseUrl;

    @Override
    public Response toResponse(MismatchedInputException e) {
        Problem p = new Problem();
        p.type = baseUrl + "/errors/validation-error";
        p.title = "Erro de validação";
        p.status = Response.Status.BAD_REQUEST.getStatusCode();
        p.detail = "Um ou mais campos não passaram na validação.";
        p.instance = (uri != null ? uri.getRequestUri().getPath() : null);
        p.timestamp = OffsetDateTime.now();
        p.errors = JsonInvalido.erros(e);
        return Response.status(p.status).type("application/problem+json").entity(p).build();
    }
}
