package com.example.retailplatform.auth.jwt.adapter.out.persistence.redis;

import com.example.retailplatform.auth.jwt.domain.model.RefreshToken;
import com.example.retailplatform.auth.jwt.domain.port.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRedisAdapter implements RefreshTokenRepositoryPort {

    private final RedisTemplate<String, RefreshToken> redisTemplate;
    private static final String PREFIX = "refreshToken:";

    @Override
    public void save(RefreshToken token) {
        String key = PREFIX + token.getToken();
        Duration ttl = Duration.between(LocalDateTime.now(), token.getExpiresAt());
        if (!ttl.isNegative()) {
            redisTemplate.opsForValue().set(key, token, ttl);
        } else {
            // Token already expired, optionally log or ignore
        }
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        RefreshToken refreshToken = redisTemplate.opsForValue().get(PREFIX + token);
        return Optional.ofNullable(refreshToken);
    }

    @Override
    public void revokeRefreshToken(String token) {
        String key = PREFIX + token;
        RefreshToken refreshToken = redisTemplate.opsForValue().get(key);
        if (refreshToken != null) {
            refreshToken.setRevoked(true);
            Duration ttl = Duration.between(LocalDateTime.now(), refreshToken.getExpiresAt());
            redisTemplate.opsForValue().set(key, refreshToken, ttl.isNegative() ? Duration.ZERO : ttl);
        }
    }

    @Override
    public void deleteByToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
