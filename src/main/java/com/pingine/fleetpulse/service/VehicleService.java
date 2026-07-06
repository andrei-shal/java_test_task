package com.pingine.fleetpulse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingine.fleetpulse.api.dto.VehicleResponse;
import com.pingine.fleetpulse.integration.VehicleEnrichment;
import com.pingine.fleetpulse.integration.VehicleRegistryClient;
import com.pingine.fleetpulse.persistence.postgres.VehicleEntity;
import com.pingine.fleetpulse.persistence.postgres.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private static final String CACHE_PREFIX = "vehicle:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final VehicleRepository vehicleRepository;
    private final VehicleRegistryClient registryClient;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public VehicleResponse getById(String vehicleId) {
        VehicleResponse cached = readCache(vehicleId);
        if (cached != null) {
            return cached;
        }
        VehicleEntity entity = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
        VehicleResponse response = toResponse(entity);
        writeCache(vehicleId, response);
        return response;
    }

    @Transactional
    public void refresh(String vehicleId) {
        redis.delete(CACHE_PREFIX + vehicleId);
        enrichFromRegistry(vehicleId);
    }

    @Transactional
    private void enrichFromRegistry(String vehicleId) {
        VehicleEntity entity = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
        Optional<VehicleEnrichment> enrichment = fetchEnrichmentSafely(vehicleId);
        enrichment.ifPresent(e -> {
            entity.setModel(e.getModel());
            entity.setVin(e.getVin());
        });
        vehicleRepository.save(entity);
        VehicleResponse response = toResponse(entity);
        writeCache(vehicleId, response);
    }

    private Optional<VehicleEnrichment> fetchEnrichmentSafely(String vehicleId) {
        try {
            return registryClient.fetchEnrichment(vehicleId);
        } catch (RuntimeException e) {
            log.warn("Vehicle registry unavailable for {}: {}", vehicleId, e.getMessage());
            return Optional.empty();
        }
    }

    private VehicleResponse readCache(String vehicleId) {
        String raw = redis.opsForValue().get(CACHE_PREFIX + vehicleId);
        if (raw == null) {
            return null;
        }
        try {
            return objectMapper.readValue(raw, VehicleResponse.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize cached vehicle {}", vehicleId, e);
            return null;
        }
    }

    private void writeCache(String vehicleId, VehicleResponse response) {
        try {
            String raw = objectMapper.writeValueAsString(response);
            redis.opsForValue().set(CACHE_PREFIX + vehicleId, raw, CACHE_TTL);
        } catch (JsonProcessingException e) {
            log.warn("Failed to cache vehicle {}", vehicleId, e);
        }
    }

    private VehicleResponse toResponse(VehicleEntity entity) {
        String driverName = entity.getDriver() != null ? entity.getDriver().getFullName() : null;
        return VehicleResponse.builder()
                .id(entity.getId())
                .licensePlate(entity.getLicensePlate())
                .model(entity.getModel())
                .vin(entity.getVin())
                .driverName(driverName)
                .build();
    }
}
