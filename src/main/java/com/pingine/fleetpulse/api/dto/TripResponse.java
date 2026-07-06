package com.pingine.fleetpulse.api.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class TripResponse {
    VehicleResponse vehicle;
    Instant startedAt;
    Instant endedAt;
    double distanceKm;
    double avgSpeedKph;
    int pointCount;
    List<PointDto> points;

    @Value
    @Builder
    public static class PointDto {
        Instant ts;
        double lat;
        double lon;
        double speedKph;
    }
}
