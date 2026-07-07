package com.mlops.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Global safety net to catch all unexpected runtime exceptions and return a 500 error,
 * preventing stack traces from leaking to the client.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // If it's a JAX-RS WebApplicationException (like 404 Not Found thrown by the framework),
        // we might want to just return its response directly.
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please try again later.");

        // We can log the actual stack trace here on the server side instead of returning it
        // java.util.logging.Logger.getLogger(GlobalExceptionMapper.class.getName()).log(java.util.logging.Level.SEVERE, exception.getMessage(), exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}
