package com.pingine.fleetpulse.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VehicleResponse {
    String id;
    String licensePlate;
    String model;
    String vin;
    String driverName;
}
