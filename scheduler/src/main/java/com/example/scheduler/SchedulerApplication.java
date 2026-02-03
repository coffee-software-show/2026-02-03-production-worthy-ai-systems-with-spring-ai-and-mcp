package com.example.scheduler;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springaicommunity.mcp.security.server.config.McpServerOAuth2Configurer.mcpServerOAuth2;

@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Bean
    Customizer<HttpSecurity> httpSecurityCustomizer(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer
    ) {
        return http -> http
                .with(mcpServerOAuth2(), a -> a.authorizationServer(issuer));
    }
}

record DogAdoptionScheduleResponse(String username, Instant appointment, int dogId) {
}

@Service
class DogAdoptionScheduler {

    @McpTool(description = "schedule an appointment to pick up or adopt a dog from a Pooch Palace location")
    DogAdoptionScheduleResponse schedule(@McpToolParam int dogId, @McpToolParam String dogName) {
        var i = Instant
                .now()
                .plus(3, ChronoUnit.DAYS);
        IO.println("scheduling " + i + " for " + dogId + '/' + dogName);
        var user = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        var appt = new DogAdoptionScheduleResponse(user, i, dogId);
        IO.println("appointment is " + appt);
        return appt;
    }

}