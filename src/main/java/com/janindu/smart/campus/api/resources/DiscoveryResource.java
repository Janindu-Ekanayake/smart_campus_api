package com.janindu.smart.campus.api.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryLinks() {
        // HATEOAS discovery endpoint
        return Response.ok(Map.of(
                "version", "1.0",
                "status", "Active",
                "_links", Map.of(
                        "rooms", "/api/v1/rooms",
                        "sensors", "/api/v1/sensors"
                )
        )).build();
    }
}