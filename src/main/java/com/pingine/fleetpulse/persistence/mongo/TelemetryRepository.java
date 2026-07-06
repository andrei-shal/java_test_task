package com.pingine.fleetpulse.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryRepository extends MongoRepository<TelemetryPoint, String>, TripQueryRepository {
}
