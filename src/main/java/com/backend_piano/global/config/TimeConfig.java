package com.backend_piano.global.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    @Bean
    public Clock systemClock(@Value("${app.time.fixed-instant:}") String fixedInstant) {
        if (fixedInstant == null || fixedInstant.isBlank()) {
            return Clock.system(SEOUL_ZONE);
        }

        return Clock.fixed(Instant.parse(fixedInstant), SEOUL_ZONE);
    }
}
