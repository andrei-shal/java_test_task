package com.pingine.fleetpulse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pingineOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pingine Fleet Pulse API")
                        .description("Telemetry ingestion and trip aggregation service for the Pingine fleet platform.")
                        .version("v1")
                        .contact(new Contact().name("Pingine Platform Team").email("platform@example.com"))
                        .license(new License().name("Proprietary")));
    }
}
