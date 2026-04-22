package com.janindu.smart.campus.api.resources;

import com.janindu.smart.campus.api.exception.RoomNotEmptyMapper.RoomNotEmptyException;
import com.janindu.smart.campus.api.model.Room;
import com.janindu.smart.campus.api.service.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        return Response.ok(new ArrayList<>(store.getRooms().values())).build();
    }

    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {
        Room room = store.getRooms().get(id);
        if (room == null) {
            return Response.status(404).entity(Map.of("error", "Room not found")).build();
        }
        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room room) {
        store.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        boolean containsSensors = store.getSensors().values().stream()
                .anyMatch(s -> s.getRoomId().equals(id));
        
        if (containsSensors) {
            throw new RoomNotEmptyException("Room " + id + " cannot be deleted because it still contains sensors.");
        }
        store.getRooms().remove(id);
        return Response.noContent().build();
    }
}