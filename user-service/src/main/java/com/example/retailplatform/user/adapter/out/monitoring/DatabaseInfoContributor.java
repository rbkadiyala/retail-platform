package com.example.retailplatform.user.adapter.out.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInfoContributor implements InfoContributor {

    private static final Logger log = LoggerFactory.getLogger("com.example.retailplatform");
    private final JdbcTemplate jdbcTemplate;

    public DatabaseInfoContributor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void contribute(Info.Builder builder) {
        String dbStatus = "UP";
        String dbError = null;

        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("Database connectivity check passed for user-service");
        } catch (Exception e) {
            dbStatus = "DOWN";
            dbError = e.getMessage();
            log.error("Database connectivity check failed for user-service: {}", e.getMessage(), e);
        }

        builder.withDetail("service", "User Service")
               .withDetail("version", "1.0.0")
               .withDetail("description", "Handles user management and authentication")
               .withDetail("dbStatus", dbStatus);

        if (dbError != null) {
            builder.withDetail("dbError", dbError);
        }
    }
}
