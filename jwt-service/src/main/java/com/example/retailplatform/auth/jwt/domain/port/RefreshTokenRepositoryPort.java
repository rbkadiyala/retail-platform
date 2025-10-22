package com.example.retailplatform.auth.jwt.domain.port;

import com.example.retailplatform.auth.jwt.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    void save(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    void revokeRefreshToken(String token);
    void deleteByToken(String token);
}
