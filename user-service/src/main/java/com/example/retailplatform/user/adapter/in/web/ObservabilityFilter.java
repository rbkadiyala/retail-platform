package com.example.retailplatform.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Order(1)
public class ObservabilityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("com.example.retailplatform");
    private static final String CORRELATION_HEADER = "X-Correlation-ID";
    private static final int MAX_BODY_LENGTH = 2000;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Generate or fetch Correlation ID
        String correlationId = wrappedRequest.getHeader(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Trace & Span IDs from OpenTelemetry
        Span currentSpan = Span.current();
        SpanContext spanContext = currentSpan.getSpanContext();

        // Put observability fields in MDC
        MDC.put("correlationId", correlationId);
        MDC.put("traceId", spanContext.isValid() ? spanContext.getTraceId() : "-");
        MDC.put("spanId", spanContext.isValid() ? spanContext.getSpanId() : "-");

        // Add Correlation ID to response headers
        wrappedResponse.setHeader(CORRELATION_HEADER, correlationId);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            String path = wrappedRequest.getRequestURI();

            // --- Conditional logging for requests ---
            if (shouldLogRequest(wrappedRequest)) {
                Map<String, Object> incomingLog = new HashMap<>();
                incomingLog.put("timestamp", OffsetDateTime.now().toString());
                incomingLog.put("service", "user-service");
                incomingLog.put("direction", "incoming");
                incomingLog.put("method", wrappedRequest.getMethod());
                incomingLog.put("uri", path);
                incomingLog.put("headers", getRequestHeaders(wrappedRequest));

                // Only read body if necessary
                if (wrappedRequest.getContentLength() > 0) {
                    incomingLog.put("body", truncateAndSanitize(
                            new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8)
                    ));
                }

                log.info(objectMapper.writeValueAsString(incomingLog));
            }

            // --- Conditional logging for responses ---
            if (shouldLogResponse(wrappedResponse)) {
                Map<String, Object> outgoingLog = new HashMap<>();
                outgoingLog.put("timestamp", OffsetDateTime.now().toString());
                outgoingLog.put("service", "user-service");
                outgoingLog.put("direction", "outgoing");
                outgoingLog.put("status", wrappedResponse.getStatus());
                outgoingLog.put("durationMs", durationMs);
                outgoingLog.put("headers", getResponseHeaders(wrappedResponse));

                // Only read body if necessary
                if (wrappedResponse.getContentSize() > 0) {
                    outgoingLog.put("body", truncateAndSanitize(
                            new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8)
                    ));
                }

                log.info(objectMapper.writeValueAsString(outgoingLog));
            }

            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    // ----------------- Conditional methods -----------------
    private boolean shouldLogRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        // Skip OPTIONS requests and actuator endpoints
        return !"OPTIONS".equalsIgnoreCase(method) && !path.startsWith("/actuator");
    }

    private boolean shouldLogResponse(HttpServletResponse response) {
        int status = response.getStatus();
        // Log only errors (4xx/5xx) or optionally all responses
        return status >= 400;
    }

    // ----------------- Helper methods -----------------
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(name -> headers.put(name, request.getHeader(name)));
        return headers;
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames().forEach(name -> headers.put(name, response.getHeader(name)));
        return headers;
    }

    private String truncateAndSanitize(String str) {
        if (str == null) return null;
        str = str.replace("\n", "\\n").replace("\r", "\\r").replace("\t", " ");
        return str.length() <= MAX_BODY_LENGTH ? str : str.substring(0, MAX_BODY_LENGTH) + "...[truncated]";
    }
}
