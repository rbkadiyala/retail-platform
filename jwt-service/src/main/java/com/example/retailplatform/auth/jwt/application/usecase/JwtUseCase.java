package com.example.retailplatform.auth.jwt.application.usecase;

import com.example.retailplatform.auth.jwt.adapter.in.web.dto.LoginResponse;

import reactor.core.publisher.Mono;

public interface JwtUseCase {
    Mono<LoginResponse> login(String username, String password);
    Mono<LoginResponse> refreshToken(String userId, String refreshToken);
}