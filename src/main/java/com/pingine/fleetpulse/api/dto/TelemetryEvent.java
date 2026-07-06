package com.pingine.fleetpulse.api.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class TelemetryEvent {
    String vehicleId;
    Instant ts;
    double lat;
    double lon;
    double speed;
    boolean ignition;
}
