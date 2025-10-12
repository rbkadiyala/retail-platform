package com.example.retailplatform.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Order(1)
public class ObservabilityFilter extends OncePerRequestFilter {

    // Use the application package logger (matches Log4j2 JSON appender filter)
    private static final Logger jsonLog = LogManager.getLogger("com.example.retailplatform");

    private static final String CORRELATION_HEADER = "X-Correlation-ID";
    private static final int MAX_BODY_LENGTH = 2000; // truncate large payloads
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Correlation ID
        String correlationId = wrappedRequest.getHeader(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        ThreadContext.put("correlationId", correlationId);
        wrappedResponse.setHeader(CORRELATION_HEADER, correlationId);

        // Trace & Span IDs
        Span currentSpan = Span.current();
        SpanContext spanContext = currentSpan.getSpanContext();
        ThreadContext.put("traceId", spanContext.isValid() ? spanContext.getTraceId() : null);
        ThreadContext.put("spanId", spanContext.isValid() ? spanContext.getSpanId() : null);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;

            // --- Incoming request log ---
            Map<String, Object> incomingLog = new HashMap<>();
            incomingLog.put("timestamp", OffsetDateTime.now().toString());
            incomingLog.put("service", "user-service");
            incomingLog.put("correlationId", correlationId);
            incomingLog.put("traceId", ThreadContext.get("traceId"));
            incomingLog.put("spanId", ThreadContext.get("spanId"));
            incomingLog.put("direction", "incoming");
            incomingLog.put("method", wrappedRequest.getMethod());
            incomingLog.put("uri", wrappedRequest.getRequestURI());
            incomingLog.put("headers", getRequestHeaders(wrappedRequest));
            incomingLog.put("body", truncateAndSanitize(new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8)));

            jsonLog.info(objectMapper.writeValueAsString(incomingLog));

            // --- Outgoing response log ---
            Map<String, Object> outgoingLog = new HashMap<>();
            outgoingLog.put("timestamp", OffsetDateTime.now().toString());
            outgoingLog.put("service", "user-service");
            outgoingLog.put("correlationId", correlationId);
            outgoingLog.put("traceId", ThreadContext.get("traceId"));
            outgoingLog.put("spanId", ThreadContext.get("spanId"));
            outgoingLog.put("direction", "outgoing");
            outgoingLog.put("status", wrappedResponse.getStatus());
            outgoingLog.put("durationMs", durationMs);
            outgoingLog.put("headers", getResponseHeaders(wrappedResponse));
            outgoingLog.put("body", truncateAndSanitize(new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8)));

            jsonLog.info(objectMapper.writeValueAsString(outgoingLog));

            wrappedResponse.copyBodyToResponse();

            ThreadContext.remove("correlationId");
            ThreadContext.remove("traceId");
            ThreadContext.remove("spanId");
        }
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator()
                .forEachRemaining(name -> headers.put(name, request.getHeader(name)));
        return headers;
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames()
                .forEach(name -> headers.put(name, response.getHeader(name)));
        return headers;
    }

    private String truncateAndSanitize(String str) {
        if (str == null) return null;
        str = str.replace("\n", "\\n").replace("\r", "\\r").replace("\t", " ");
        return str.length() <= MAX_BODY_LENGTH ? str : str.substring(0, MAX_BODY_LENGTH) + "...[truncated]";
    }
}
