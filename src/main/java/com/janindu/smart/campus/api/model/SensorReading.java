package com.janindu.smart.campus.api.model;

public class SensorReading {
    private String timestamp;
    private double value;

    public SensorReading() {}

    public SensorReading(String timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}