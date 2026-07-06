package com.pingine.fleetpulse.messaging;

import com.pingine.fleetpulse.api.dto.TelemetryEvent;
import com.pingine.fleetpulse.config.JacksonConfig;
import com.pingine.fleetpulse.config.RabbitConfig;
import com.pingine.fleetpulse.persistence.mongo.TelemetryRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TelemetryConsumerIT.TestApp.class)
@Testcontainers
class TelemetryConsumerIT {

    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            FlywayAutoConfiguration.class,
            RedisAutoConfiguration.class,
            RedisRepositoriesAutoConfiguration.class
    })
    @EnableMongoRepositories(basePackageClasses = TelemetryRepository.class)
    @ComponentScan(basePackageClasses = {TelemetryConsumer.class, RabbitConfig.class, JacksonConfig.class})
    static class TestApp {
    }

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management-alpine");

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6");

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TelemetryRepository telemetryRepository;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @BeforeEach
    void clean() {
        telemetryRepository.deleteAll();
    }

    @Test
    void persistsIncomingTelemetry() {
        TelemetryEvent event = new TelemetryEvent(
                "v1",
                Instant.parse("2026-04-27T08:00:00Z"),
                52.52, 13.40, 0.0, true);

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);

        Awaitility.await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(telemetryRepository.count()).isEqualTo(1L));
    }
}
