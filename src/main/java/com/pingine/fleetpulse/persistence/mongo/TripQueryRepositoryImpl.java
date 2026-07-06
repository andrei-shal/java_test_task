package com.pingine.fleetpulse.persistence.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class TripQueryRepositoryImpl implements TripQueryRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<TelemetryPoint> findRecentPoints(String vehicleId, int limit) {
        Query query = new Query()
                .addCriteria(Criteria.where("vehicleId").is(vehicleId))
                .with(Sort.by(Sort.Direction.DESC, "ts"))
                .limit(limit);
        return mongoTemplate.find(query, TelemetryPoint.class);
    }
}
