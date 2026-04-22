package com.janindu.smart.campus.api.resources;

import com.janindu.smart.campus.api.exception.SensorUnavailableMapper.SensorUnavailableException;
import com.janindu.smart.campus.api.model.Sensor;
import com.janindu.smart.campus.api.model.SensorReading;
import com.janindu.smart.campus.api.service.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

// No @Path annotation needed here because it is handled by the Sub-Resource Locator in SensorResource
public class SensorReadingResource {
    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        if (!store.getSensors().containsKey(sensorId)) {
            return Response.status(404).entity(Map.of("error", "Sensor not found")).build();
        }
        List<SensorReading> readings = store.getSensorReadings().getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity(Map.of("error", "Sensor not found")).build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Cannot add reading: Sensor " + sensorId + " is in maintenance.");
        }

        reading.setTimestamp(String.valueOf(System.currentTimeMillis()));
        sensor.setCurrentValue(reading.getValue()); // Update parent state

        store.getSensorReadings().putIfAbsent(sensorId, new CopyOnWriteArrayList<>());
        store.getSensorReadings().get(sensorId).add(reading);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}