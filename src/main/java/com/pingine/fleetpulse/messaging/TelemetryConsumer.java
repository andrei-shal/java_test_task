package com.pingine.fleetpulse.messaging;

import com.pingine.fleetpulse.api.dto.TelemetryEvent;
import com.pingine.fleetpulse.persistence.mongo.TelemetryPoint;
import com.pingine.fleetpulse.persistence.mongo.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryConsumer {

    public static final String QUEUE = "vehicle.telemetry.v1";

    private final TelemetryRepository repository;

    @RabbitListener(queues = QUEUE)
    public void onEvent(TelemetryEvent event) {
        log.debug("Received telemetry for vehicle {} at {}", event.getVehicleId(), event.getTs());
        TelemetryPoint point = new TelemetryPoint();
        point.setVehicleId(event.getVehicleId());
        point.setTs(LocalDateTime.ofInstant(event.getTs(), ZoneOffset.UTC));
        point.setLat(event.getLat());
        point.setLon(event.getLon());
        point.setSpeed(event.getSpeed());
        point.setIgnition(event.isIgnition());
        repository.save(point);
    }
}
