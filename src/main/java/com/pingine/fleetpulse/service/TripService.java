package com.pingine.fleetpulse.service;

import com.pingine.fleetpulse.api.dto.TripResponse;

public interface TripService {

    TripResponse getLastTrip(String vehicleId);
}
