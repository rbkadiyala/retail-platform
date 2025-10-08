package com.example.retailplatform.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final WebClient webClient = WebClient.create(
            "http://keycloak:8080/realms/my-app-realm/protocol/openid-connect"
    );

    @PostMapping
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String requestBody = "grant_type=password&client_id=auth-service" +
                "&client_secret=auth-service-secret" +
                "&username=" + username +
                "&password=" + password;

        Map<String, String> tokenResponse = webClient.post()
                .uri("/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestParam(defaultValue = "world") String name) {
        return ResponseEntity.ok("Auth Service is running, hello " + name + "!");
    }
}
