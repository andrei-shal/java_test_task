package com.pingine.fleetpulse.api;

import com.pingine.fleetpulse.api.dto.TripResponse;
import com.pingine.fleetpulse.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Trips")
public class TripController {

    private final TripService tripService;

    @GetMapping("/{vehicleId}/last-trip")
    @Operation(summary = "Get the most recent completed trip for a vehicle")
    public TripResponse getLastTrip(@PathVariable String vehicleId) {
        return tripService.getLastTrip(vehicleId);
    }
}
