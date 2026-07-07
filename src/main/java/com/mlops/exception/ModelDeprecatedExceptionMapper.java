package com.mlops.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class ModelDeprecatedExceptionMapper implements ExceptionMapper<ModelDeprecatedException> {

    @Override
    public Response toResponse(ModelDeprecatedException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Forbidden");
        error.put("message", exception.getMessage());

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .build();
    }
}
