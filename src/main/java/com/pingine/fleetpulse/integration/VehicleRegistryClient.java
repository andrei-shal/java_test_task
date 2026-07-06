package com.pingine.fleetpulse.integration;

import lombok.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(
        name = "vehicle-registry",
        url = "${vehicle-registry.url}",
        configuration = VehicleRegistryFeignConfig.class
)
public interface VehicleRegistryClient {

    @PostMapping("/enrichments")
    Optional<VehicleEnrichment> fetchEnrichment(@RequestBody EnrichmentRequest request);

    default Optional<VehicleEnrichment> fetchEnrichment(String vehicleId) {
        return fetchEnrichment(new EnrichmentRequest(vehicleId));
    }

    @Value
    class EnrichmentRequest {
        String vehicleId;
    }
}
