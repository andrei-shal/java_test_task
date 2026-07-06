package com.pingine.fleetpulse.api;

import com.pingine.fleetpulse.api.dto.VehicleResponse;
import com.pingine.fleetpulse.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get vehicle by id")
    public VehicleResponse getById(@PathVariable String vehicleId) {
        return vehicleService.getById(vehicleId);
    }

    @PostMapping("/{vehicleId}/refresh")
    @Operation(summary = "Refresh vehicle metadata from the vehicle registry")
    public void refresh(@PathVariable String vehicleId) {
        vehicleService.refresh(vehicleId);
    }
}
