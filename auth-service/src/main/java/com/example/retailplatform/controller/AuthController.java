package com.example.retailplatform.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token retrieval")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final WebClient webClient;

    public AuthController(WebClient keycloakWebClient) {
        this.webClient = keycloakWebClient;
    }

    @Operation(
            summary = "Login using username and password",
            description = "Authenticates the user against Keycloak using Resource Owner Password flow and returns an access token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        log.info("Login attempt for username: {}", loginRequest.getUsername());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "auth-service");
        formData.add("client_secret", "auth-service-secret");
        formData.add("username", loginRequest.getUsername());
        formData.add("password", loginRequest.getPassword());

        try {
            Map<String, String> tokenResponse = webClient.post()
                    .uri("/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .block();

            log.info("Login successful for username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(tokenResponse);

        } catch (WebClientResponseException e) {
            log.warn("Login failed for username: {} - Status: {} Body: {}",
                    loginRequest.getUsername(), e.getStatusCode().value(), e.getResponseBodyAsString());
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));

        } catch (Exception e) {
            log.error("Internal error during login for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        } finally {
            MDC.clear();
        }
    }

    @Operation(
            summary = "Health check / test endpoint",
            description = "Simple test endpoint to verify if the Auth Service is running."
    )
    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestParam(defaultValue = "world") String name) {
        log.info("Test endpoint called with name: {}", name);
        return ResponseEntity.ok("Auth Service is running, hello " + name + "!");
    }
}
