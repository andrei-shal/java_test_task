package com.pingine.fleetpulse.service.trip;

import com.pingine.fleetpulse.domain.Trip;
import com.pingine.fleetpulse.persistence.mongo.TelemetryPoint;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class TripDetector {

    public List<Trip> detect(List<TelemetryPoint> points) {
        List<TelemetryPoint> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparing(TelemetryPoint::getTs));

        List<Trip.TripPoint> tripPoints = new ArrayList<>();
        double distance = 0;
        String vehicleId = null;
        List<Trip> result = new ArrayList<>();

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
                Trip.TripPoint last = tripPoints.get(tripPoints.size() - 1);

                if (newPoint.getTs().equals(last.getTs())) {
                    continue;
                }

                distance += GeoDistance.haversineKm(
                        last.getLat(), last.getLon(),
                        point.getLat(), point.getLon());
            }

            tripPoints.add(newPoint);

            if (!point.isIgnition()) {
                Trip.TripPoint first = tripPoints.get(0);
                Trip.TripPoint last = tripPoints.get(tripPoints.size() - 1);
                long durationSec = last.getTs().getEpochSecond()
                        - first.getTs().getEpochSecond();
                double avgSpeed = durationSec > 0
                        ? distance / (durationSec / 3600.0)
                        : 0;

                result.add(Trip.builder()
                        .vehicleId(vehicleId)
                        .startedAt(first.getTs())
                        .endedAt(last.getTs())
                        .distanceKm(distance)
                        .avgSpeedKph(avgSpeed)
                        .points(tripPoints)
                        .build());

                tripPoints = new ArrayList<>();
                distance = 0;
                vehicleId = null;
            }
        }

        return result;
    }
}
