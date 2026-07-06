package com.pingine.fleetpulse.service;

import com.pingine.fleetpulse.api.dto.TripResponse;
import com.pingine.fleetpulse.api.dto.TripResponse.PointDto;
import com.pingine.fleetpulse.api.dto.VehicleResponse;
import com.pingine.fleetpulse.domain.Trip;
import com.pingine.fleetpulse.persistence.mongo.TelemetryPoint;
import com.pingine.fleetpulse.persistence.mongo.TelemetryRepository;
import com.pingine.fleetpulse.service.trip.TripDetector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private static final int MAX_POINTS = 1000;

    private final TelemetryRepository telemetryRepository;
    private final TripDetector tripDetector;
    private final VehicleService vehicleService;

    @Override
    public TripResponse getLastTrip(String vehicleId) {
        List<TelemetryPoint> points =
            telemetryRepository.findRecentPoints(vehicleId, MAX_POINTS);

        List<Trip> trips = tripDetector.detect(points);

        if (trips.isEmpty()) {
            throw new VehicleNotFoundException(vehicleId);
        }

        Trip lastTrip = trips.get(trips.size() - 1);
        VehicleResponse vehicle = vehicleService.getById(vehicleId);

        return TripResponse.builder()
            .vehicle(vehicle)
            .startedAt(lastTrip.getStartedAt())
            .endedAt(lastTrip.getEndedAt())
            .distanceKm(lastTrip.getDistanceKm())
            .avgSpeedKph(lastTrip.getAvgSpeedKph())
            .pointCount(lastTrip.getPoints().size())
            .points(lastTrip.getPoints().stream()
                .map(p -> PointDto.builder()
                    .ts(p.getTs())
                    .lat(p.getLat())
                    .lon(p.getLon())
                    .speedKph(p.getSpeedKph())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
