package com.example.retailplatform.auth.jwt.domain.port;

import com.example.retailplatform.auth.jwt.domain.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {
    void save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
}
