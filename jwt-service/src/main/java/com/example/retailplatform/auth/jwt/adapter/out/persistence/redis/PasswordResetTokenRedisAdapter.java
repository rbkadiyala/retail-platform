package com.example.retailplatform.auth.jwt.adapter.out.persistence.redis;

import com.example.retailplatform.auth.jwt.domain.model.PasswordResetToken;
import com.example.retailplatform.auth.jwt.domain.port.PasswordResetTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRedisAdapter implements PasswordResetTokenRepositoryPort {

    private final RedisTemplate<String, PasswordResetToken> redisTemplate;
    private static final String PREFIX = "passwordResetToken:";

    @Override
    public void save(PasswordResetToken token) {
        String key = PREFIX + token.getToken();
        redisTemplate.opsForValue().set(key, token, Duration.between(token.getExpiresAt(), java.time.LocalDateTime.now()).abs());
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        PasswordResetToken resetToken = redisTemplate.opsForValue().get(PREFIX + token);
        return Optional.ofNullable(resetToken);
    }
}
