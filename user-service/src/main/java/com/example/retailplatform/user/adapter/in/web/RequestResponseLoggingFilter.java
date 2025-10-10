package com.example.retailplatform.user.adapter.in.web;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(2)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LogManager.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        log.info("Incoming request: method={}, uri={}, headers={}",
                 request.getMethod(),
                 request.getRequestURI(),
                 getHeaders(request));

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Outgoing response: status={}, durationMs={}, headers={}",
                     response.getStatus(),
                     duration,
                     getHeaders(response));
        }
    }

    private String getHeaders(HttpServletRequest request) {
        var sb = new StringBuilder();
        request.getHeaderNames().asIterator()
               .forEachRemaining(name -> sb.append(name).append("=").append(request.getHeader(name)).append("; "));
        return sb.toString();
    }

    private String getHeaders(HttpServletResponse response) {
        var sb = new StringBuilder();
        response.getHeaderNames()
                .forEach(name -> sb.append(name).append("=").append(response.getHeader(name)).append("; "));
        return sb.toString();
    }
}
