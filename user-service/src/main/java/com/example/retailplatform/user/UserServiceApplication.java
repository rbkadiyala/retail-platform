package com.example.retailplatform.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
@EnableAsync
public class UserServiceApplication {
    private static final Logger log = LogManager.getLogger(UserServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);

        // Test log
        log.info("Logging is working! This should appear in /app/logs/user-service.log");
    }

    
}