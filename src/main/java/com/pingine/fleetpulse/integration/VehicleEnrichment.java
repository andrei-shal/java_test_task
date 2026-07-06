package com.pingine.fleetpulse.integration;

import lombok.Value;

@Value
public class VehicleEnrichment {
    String vehicleId;
    String model;
    String vin;
}
