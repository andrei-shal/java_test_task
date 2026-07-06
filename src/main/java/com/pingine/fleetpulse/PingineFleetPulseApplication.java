package com.pingine.fleetpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.pingine.fleetpulse.integration")
public class PingineFleetPulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingineFleetPulseApplication.class, args);
    }
}
