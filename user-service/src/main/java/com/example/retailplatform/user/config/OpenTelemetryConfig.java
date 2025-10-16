package com.example.retailplatform.user.config;

import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test") // Skip OpenTelemetry during tests
public class OpenTelemetryConfig {

    @PostConstruct
    public void initOpenTelemetry() {
        try {
            SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                    .setResource(Resource.getDefault())
                    .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                    .build();

            OpenTelemetrySdk.builder()
                    .setTracerProvider(tracerProvider)
                    .buildAndRegisterGlobal();

        } catch (IllegalStateException e) {
            // Already initialized globally, ignore
        }
    }
}
