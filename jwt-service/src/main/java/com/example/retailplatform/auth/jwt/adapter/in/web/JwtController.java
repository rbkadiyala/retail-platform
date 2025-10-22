package com.example.retailplatform.auth.jwt.adapter.in.web;

import com.example.retailplatform.auth.jwt.adapter.in.web.dto.LoginRequest;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.LoginResponse;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.RefreshTokenRequest;
import com.example.retailplatform.auth.jwt.application.usecase.JwtUseCase;
import com.example.retailplatform.auth.jwt.exception.InvalidCredentialsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/auth/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final JwtUseCase jwtUseCase;

    @Operation(summary = "Authenticate user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return jwtUseCase.login(request.getUsername(), request.getPassword())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Login failed: {}", e.getMessage());
                    if (e instanceof InvalidCredentialsException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Operation(summary = "Refresh access token using a refresh token")
    @PostMapping("/refresh")
    public Mono<ResponseEntity<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return jwtUseCase.refreshToken(request.getUserId(), request.getRefreshToken())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Refresh token failed for user {}: {}", request.getUserId(), e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }
}
