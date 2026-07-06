package com.pingine.fleetpulse.persistence.mongo;

import java.util.List;

public interface TripQueryRepository {

    List<TelemetryPoint> findRecentPoints(String vehicleId, int limit);
}
