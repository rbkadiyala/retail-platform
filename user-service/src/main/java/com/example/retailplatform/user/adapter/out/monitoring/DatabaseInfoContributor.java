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
        boolean adminExists = false;

        try {
            // ✅ Simple query to check database connectivity
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("Database connectivity check passed for user-service");

            // ✅ Check if admin user exists (using non-deprecated API)
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users WHERE username = ? AND active = true",
                    Integer.class,
                    "admin"   // varargs, no Object[]
            );

            adminExists = count != null && count > 0;
            log.info("Admin user exists: {}", adminExists);

        } catch (Exception e) {
            dbStatus = "DOWN";
            dbError = e.getMessage();
            log.error("Database connectivity check failed for user-service: {}", e.getMessage(), e);
        }

        builder.withDetail("service", "User Service")
               .withDetail("version", "1.0.0")
               .withDetail("description", "Handles user management and authentication")
               .withDetail("dbStatus", dbStatus)
               .withDetail("adminUserExists", adminExists);

        if (dbError != null) {
            builder.withDetail("dbError", dbError);
        }
    }
}
