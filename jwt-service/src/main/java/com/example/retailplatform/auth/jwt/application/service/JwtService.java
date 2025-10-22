package com.example.retailplatform.auth.jwt.application.service;

import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalJwtUserResponse;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.LoginResponse;
import com.example.retailplatform.auth.jwt.application.usecase.JwtUseCase;
import com.example.retailplatform.auth.jwt.domain.port.UserClientPort;
import com.example.retailplatform.auth.jwt.exception.InvalidCredentialsException;
import com.example.retailplatform.auth.jwt.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService implements JwtUseCase {

    private final UserClientPort userClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<LoginResponse> login(String username, String password) {
        return userClient.authenticate(username, password)
                .flatMap(authResponse -> {
                    if (Boolean.TRUE.equals(authResponse.isAuthenticated()) && authResponse.getUser() != null) {
                        InternalJwtUserResponse user = authResponse.getUser();

                        String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
                        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

                        return storeTokensInRedis(user, accessToken, refreshToken)
                                .thenReturn(LoginResponse.builder()
                                        .accessToken(accessToken)
                                        .tokenType("Bearer")
                                        .refreshToken(refreshToken)
                                        .expiresIn(jwtTokenProvider.getExpirationInMs())
                                        .user(user)
                                        .build());
                    } else {
                        return Mono.error(new InvalidCredentialsException());
                    }
                });
    }

    @Override
    public Mono<LoginResponse> refreshToken(String userId, String refreshToken) {
        return Mono.fromCallable(() -> {
            String storedRefreshToken = redisTemplate.opsForValue().get("jwt-refresh:" + userId);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new InvalidCredentialsException();
            }

            String userJson = redisTemplate.opsForValue().get("jwt-user:" + userId);
            if (userJson == null) throw new InvalidCredentialsException();

            InternalJwtUserResponse user = objectMapper.readValue(userJson, InternalJwtUserResponse.class);
            String newAccessToken = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());

            redisTemplate.opsForValue().set(
                    "jwt:" + userId, newAccessToken, jwtTokenProvider.getExpirationInMs(), TimeUnit.MILLISECONDS
            );

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .refreshToken(refreshToken)
                    .expiresIn(jwtTokenProvider.getExpirationInMs())
                    .user(user)
                    .build();
        });
    }

    private Mono<Void> storeTokensInRedis(InternalJwtUserResponse user, String accessToken, String refreshToken) {
        return Mono.fromRunnable(() -> {
            try {
                String userJson = objectMapper.writeValueAsString(user);
                redisTemplate.opsForValue().set("jwt:" + user.getId(), accessToken, jwtTokenProvider.getExpirationInMs(), TimeUnit.MILLISECONDS);
                redisTemplate.opsForValue().set("jwt-refresh:" + user.getId(), refreshToken, jwtTokenProvider.getRefreshExpirationInMs(), TimeUnit.MILLISECONDS);
                redisTemplate.opsForValue().set("jwt-user:" + user.getId(), userJson, jwtTokenProvider.getRefreshExpirationInMs(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store tokens in Redis", e);
            }
        });
    }
}
