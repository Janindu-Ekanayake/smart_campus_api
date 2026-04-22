package com.janindu.smart.campus.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableMapper.SensorUnavailableException> {
    
    public static class SensorUnavailableException extends RuntimeException {
        public SensorUnavailableException(String message) { super(message); }
    }

    @Override
    public Response toResponse(SensorUnavailableException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(Map.of("status", 403, "error", "Forbidden", "message", e.getMessage()))
                .build();
    }
}