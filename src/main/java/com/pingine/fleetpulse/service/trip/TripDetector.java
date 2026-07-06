package com.pingine.fleetpulse.service.trip;

import com.pingine.fleetpulse.domain.Trip;
import com.pingine.fleetpulse.persistence.mongo.TelemetryPoint;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Component
public class TripDetector {

    public List<Trip> detect(List<TelemetryPoint> points) {
        List<TelemetryPoint> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparing(TelemetryPoint::getTs));

        List<Trip.TripPoint> tripPoints = new LinkedList<>();
        double distance = 0;
        String vehicleId = null;
        List<Trip> result = new LinkedList<>();

        for (TelemetryPoint point : sorted) {
            if (tripPoints.isEmpty()) {
                if (!point.isIgnition()) {
                    continue;
                }
                vehicleId = point.getVehicleId();
            }

            Trip.TripPoint newPoint = Trip.TripPoint.builder()
                    .ts(point.getTs().atZone(ZoneOffset.UTC).toInstant())
                    .lat(point.getLat())
                    .lon(point.getLon())
                    .speedKph(point.getSpeed())
                    .build();

            if (!tripPoints.isEmpty()) {
                Trip.TripPoint last = tripPoints.getLast();

                if (newPoint.equals(last)) {
                    continue;
                }

                distance += GeoDistance.haversineKm(
                        last.getLat(), last.getLon(),
                        point.getLat(), point.getLon());
            }

            tripPoints.add(newPoint);

            if (!point.isIgnition()) {
                long durationSec = tripPoints.getLast().getTs().getEpochSecond()
                        - tripPoints.getFirst().getTs().getEpochSecond();
                double avgSpeed = durationSec > 0
                        ? distance / (durationSec / 3600.0)
                        : 0;

                result.add(Trip.builder()
                        .vehicleId(vehicleId)
                        .startedAt(tripPoints.getFirst().getTs())
                        .endedAt(tripPoints.getLast().getTs())
                        .distanceKm(distance)
                        .avgSpeedKph(avgSpeed)
                        .points(tripPoints)
                        .build());

                tripPoints = new LinkedList<>();
                distance = 0;
                vehicleId = null;
            }
        }

        return result;
    }
}
