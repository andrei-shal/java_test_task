package com.pingine.fleetpulse.api;

import com.pingine.fleetpulse.api.dto.TripResponse;
import com.pingine.fleetpulse.api.dto.VehicleResponse;
import com.pingine.fleetpulse.service.TripService;
import com.pingine.fleetpulse.service.VehicleNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @Test
    void returnsTripForExistingVehicle() throws Exception {
        TripResponse response = TripResponse.builder()
                .vehicle(VehicleResponse.builder()
                        .id("v1")
                        .licensePlate("B-PG-1001")
                        .model("Mercedes Actros")
                        .vin("TESTVIN0000000001")
                        .driverName("Driver One")
                        .build())
                .startedAt(Instant.parse("2026-04-27T08:00:00Z"))
                .endedAt(Instant.parse("2026-04-27T08:30:00Z"))
                .distanceKm(12.5)
                .avgSpeedKph(25.0)
                .pointCount(5)
                .points(List.of())
                .build();

        when(tripService.getLastTrip("v1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/vehicles/v1/last-trip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.id").value("v1"))
                .andExpect(jsonPath("$.vehicle.model").value("Mercedes Actros"))
                .andExpect(jsonPath("$.vehicle.driverName").value("Driver One"))
                .andExpect(jsonPath("$.startedAt").value("2026-04-27T08:00:00Z"))
                .andExpect(jsonPath("$.endedAt").value("2026-04-27T08:30:00Z"))
                .andExpect(jsonPath("$.distanceKm").value(12.5))
                .andExpect(jsonPath("$.avgSpeedKph").value(25.0))
                .andExpect(jsonPath("$.pointCount").value(5));
    }

    @Test
    void returns404WhenNoTrips() throws Exception {
        when(tripService.getLastTrip("v1")).thenThrow(new VehicleNotFoundException("v1"));

        mockMvc.perform(get("/api/v1/vehicles/v1/last-trip"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Vehicle not found: v1"));
    }
}
