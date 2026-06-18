package com.backend_piano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BackendPianoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendPianoApplication.class, args);
    }

}
