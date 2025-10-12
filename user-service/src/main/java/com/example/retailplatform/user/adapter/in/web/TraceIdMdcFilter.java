package com.example.retailplatform.user.adapter.in.web;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(0)
public class TraceIdMdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Fetch tracer lazily
        Tracer tracer = GlobalOpenTelemetry.getTracer("user-service");

        Span span = tracer.spanBuilder(request.getMethod() + " " + request.getRequestURI())
                .setSpanKind(SpanKind.SERVER)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            SpanContext context = span.getSpanContext();
            MDC.put("traceId", context.isValid() ? context.getTraceId() : "-");
            MDC.put("spanId", context.isValid() ? context.getSpanId() : "-");

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
            MDC.remove("spanId");
            span.end();
        }
    }
}