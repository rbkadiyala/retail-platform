package com.example.retailplatform.user.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * This configuration disables real OpenTelemetry during tests
 * by providing a no-op implementation of OpenTelemetry and Tracer.
 */
@TestConfiguration
public class TestOpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        // No-op OpenTelemetry instance for tests
        return OpenTelemetry.noop();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("test-tracer");
    }
}
