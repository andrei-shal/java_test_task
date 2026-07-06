package com.pingine.fleetpulse.config;

import com.pingine.fleetpulse.messaging.TelemetryConsumer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "vehicle.events";
    public static final String ROUTING_KEY = "telemetry.v1";

    @Bean
    public TopicExchange vehicleExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue telemetryQueue() {
        return new Queue(TelemetryConsumer.QUEUE, true);
    }

    @Bean
    public Binding telemetryBinding(Queue telemetryQueue, TopicExchange vehicleExchange) {
        return BindingBuilder.bind(telemetryQueue).to(vehicleExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
