package com.pingine.fleetpulse.persistence.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "telemetry_points")
@Getter
@Setter
public class TelemetryPoint {

    @Id
    private String id;

    private String vehicleId;
    private LocalDateTime ts;
    private double lat;
    private double lon;
    private double speed;
    private boolean ignition;
}
