package com.pingine.fleetpulse.service.trip;

import com.pingine.fleetpulse.domain.Trip;
import com.pingine.fleetpulse.persistence.mongo.TelemetryPoint;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Splits a stream of telemetry points into completed trips.
 * A trip starts on ignition=true and ends on the next ignition=false.
 */
@Component
public class TripDetector {

    public List<Trip> detect(List<TelemetryPoint> points) {
        List<TelemetryPoint> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparing(TelemetryPoint::getTs));
           
        Map<String, List<Trip.TripPoint>> trips = new HashMap<>();
        Map<String, Double> tripDistances = new HashMap<>();
        List<Trip> result = new LinkedList<>();

        for (TelemetryPoint point : sorted) {
          if (!trips.containsKey(point.getVehicleId())) {
            if (point.isIgnition()) {
              trips.put(point.getVehicleId(), new LinkedList<Trip.TripPoint>());
              tripDistances.put(point.getVehicleId(), .0);
            } else {
              continue;
            }
          }

          List<Trip.TripPoint> tripPoints = trips.get(point.getVehicleId());
          
          if (!tripPoints.isEmpty()) {
            Trip.TripPoint lastPoint = tripPoints.getLast();
            double lastPointDistance = GeoDistance.haversineKm(lastPoint.getLat(), lastPoint.getLon(), point.getLat(), point.getLon());

            tripDistances.merge(point.getVehicleId(), lastPointDistance, Double::sum);
          }

          tripPoints.add(
            Trip.TripPoint
              .builder()
              .ts(point.getTs().atZone(ZoneOffset.UTC).toInstant())
              .lat(point.getLat())
              .lon(point.getLon())
              .speedKph(point.getSpeed())
              .build()
          );
            
          if (!point.isIgnition()) {
            double distance = tripDistances.get(point.getVehicleId());
            long durationSeconds = tripPoints.getLast().getTs().getEpochSecond()
                - tripPoints.getFirst().getTs().getEpochSecond();
            double avgSpeed = durationSeconds > 0
                ? distance / (durationSeconds / 3600.0)
                : 0;

            Trip trip = Trip.builder()
              .vehicleId(point.getVehicleId())
              .startedAt(tripPoints.getFirst().getTs())
              .endedAt(tripPoints.getLast().getTs())
              .distanceKm(distance)
              .avgSpeedKph(avgSpeed)
              .points(tripPoints)
              .build();

            result.add(trip);
            
            trips.remove(point.getVehicleId());
            tripDistances.remove(point.getVehicleId());
          }
        }

        return result;
    }
}
