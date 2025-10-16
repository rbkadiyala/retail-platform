package com.example.retailplatform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/login")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final WebClient webClient;

    public AuthController(WebClient keycloakWebClient) {
        this.webClient = keycloakWebClient;
    }

    /**
     * Login using Keycloak Resource Owner Password Credentials Flow.
     * Returns access token and related info.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        // Generate a request-specific correlation ID for observability
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        log.info("Login attempt for username: {}", username);

        // Form URL-encoded body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "auth-service");
        formData.add("client_secret", "auth-service-secret");
        formData.add("username", username);
        formData.add("password", password);

        try {
            Map<String, String> tokenResponse = webClient.post()
                    .uri("/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .block();

            log.info("Login successful for username: {}", username);
            return ResponseEntity.ok(tokenResponse);

        } catch (WebClientResponseException e) {
            log.warn("Login failed for username: {} - Status: {} Body: {}", 
                     username, e.getRawStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getRawStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));

        } catch (Exception e) {
            log.error("Internal error during login for username: {}", username, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error"));
        } finally {
            MDC.clear();
        }
    }

    /**
     * Simple health/test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestParam(defaultValue = "world") String name) {
        log.info("Test endpoint called with name: {}", name);
        return ResponseEntity.ok("Auth Service is running, hello " + name + "!");
    }
}
