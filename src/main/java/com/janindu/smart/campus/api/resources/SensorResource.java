package com.janindu.smart.campus.api.resources;

import com.janindu.smart.campus.api.exception.LinkedNotFoundMapper.LinkedNotFoundException;
import com.janindu.smart.campus.api.model.Sensor;
import com.janindu.smart.campus.api.service.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(store.getSensors().values());
        if (type != null && !type.isEmpty()) {
            result = result.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return Response.ok(result).build();
    }

    @POST
    public Response createSensor(Sensor sensor) {
        if (!store.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedNotFoundException("Cannot link: Room ID " + sensor.getRoomId() + " does not exist.");
        }
        store.getSensors().put(sensor.getId(), sensor);
        store.getSensorReadings().putIfAbsent(sensor.getId(), new CopyOnWriteArrayList<>());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // SUB-RESOURCE LOCATOR
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}