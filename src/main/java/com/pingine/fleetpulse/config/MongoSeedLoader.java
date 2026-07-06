package com.pingine.fleetpulse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingine.fleetpulse.api.dto.TelemetryEvent;
import com.pingine.fleetpulse.persistence.mongo.TelemetryPoint;
import com.pingine.fleetpulse.persistence.mongo.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "fleet-pulse.seed.enabled", havingValue = "true")
@Slf4j
public class MongoSeedLoader implements ApplicationRunner {

    private static final String RESOURCE_PATH = "seed/telemetry-sample.ndjson";

    private final TelemetryRepository telemetryRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (telemetryRepository.count() > 0) {
            log.info("Telemetry collection is not empty, skipping seed.");
            return;
        }
        List<TelemetryPoint> points = readPoints();
        telemetryRepository.saveAll(points);
        log.info("Loaded {} telemetry points from {}", points.size(), RESOURCE_PATH);
    }

    private List<TelemetryPoint> readPoints() throws Exception {
        List<TelemetryPoint> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource(RESOURCE_PATH).getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                TelemetryEvent event = objectMapper.readValue(line, TelemetryEvent.class);
                TelemetryPoint point = new TelemetryPoint();
                point.setVehicleId(event.getVehicleId());
                point.setTs(LocalDateTime.ofInstant(event.getTs(), ZoneOffset.UTC));
                point.setLat(event.getLat());
                point.setLon(event.getLon());
                point.setSpeed(event.getSpeed());
                point.setIgnition(event.isIgnition());
                result.add(point);
            }
        }
        return result;
    }
}
