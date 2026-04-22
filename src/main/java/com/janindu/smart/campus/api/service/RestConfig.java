package com.janindu.smart.campus.api.service;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// DO NOT CHANGE THIS PATH! The coursework rubric explicitly requires "/api/v1" for maximum marks in Task 1.1.
@ApplicationPath("/api/v1")
public class RestConfig extends Application {
    // Triggers JAX-RS auto-discovery for all resources and providers
}