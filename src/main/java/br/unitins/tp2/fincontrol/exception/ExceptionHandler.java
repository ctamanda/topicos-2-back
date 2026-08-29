package br.unitins.tp2.fincontrol.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class ExceptionHandler implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", exception.getMessage());
        response.put("errors", exception.getErros());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}