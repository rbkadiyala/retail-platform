package com.example.retailplatform.user.adapter.in.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger httpLogger = LogManager.getLogger("HTTP_LOG");
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "token", "secret", "authorization", "apiKey"
    );

    private final ObjectMapper objectMapper;

    public RequestResponseLoggingFilter() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        wrappedResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
        ThreadContext.put(CORRELATION_ID_HEADER, correlationId);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logRequest(wrappedRequest, correlationId);
            logResponse(wrappedRequest, wrappedResponse, correlationId, duration);

            ThreadContext.remove(CORRELATION_ID_HEADER);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String correlationId) {
        Map<String, String> params = Collections.list(request.getParameterNames())
                .stream()
                .collect(Collectors.toMap(p -> p, request::getParameter));

        String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        body = maskSensitiveData(body);
        body = prettyPrintJson(body);

        httpLogger.info(
                "{{\"correlationId\":\"{}\",\"method\":\"{}\",\"path\":\"{}\",\"params\":{},\"body\":{}}}",
                correlationId,
                request.getMethod(),
                request.getRequestURI(),
                params.isEmpty() ? "{}" : params,
                body.isEmpty() ? "{}" : body
        );
    }

    private void logResponse(ContentCachingRequestWrapper request,
                             ContentCachingResponseWrapper response,
                             String correlationId,
                             long duration) {

        int status = response.getStatus();

        String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        body = maskSensitiveData(body);
        body = prettyPrintJson(body);

        httpLogger.info(
                "{{\"correlationId\":\"{}\",\"method\":\"{}\",\"path\":\"{}\",\"status\":{},\"durationMs\":{},\"body\":{}}}",
                correlationId,
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration,
                body.isEmpty() ? "{}" : body
        );
    }

    private String maskSensitiveData(String json) {
        if (json == null || json.isEmpty()) return json;
        String masked = json;
        for (String field : SENSITIVE_FIELDS) {
            masked = masked.replaceAll(
                    "(?i)(\"" + field + "\"\\s*:\\s*\").*?(\")",
                    "$1***$2"
            );
        }
        return masked;
    }

    private String prettyPrintJson(String json) {
        if (json == null || json.isEmpty()) return json;
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // not valid JSON, return original string
            return json;
        }
    }
}
