package com.janindu.smart.campus.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class LinkedNotFoundMapper implements ExceptionMapper<LinkedNotFoundMapper.LinkedNotFoundException> {
    
    public static class LinkedNotFoundException extends RuntimeException {
        public LinkedNotFoundException(String message) { super(message); }
    }

    @Override
    public Response toResponse(LinkedNotFoundException e) {
        return Response.status(422)
                .entity(Map.of("status", 422, "error", "Unprocessable Entity", "message", e.getMessage()))
                .build();
    }
}