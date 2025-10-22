package com.example.retailplatform.auth.jwt.domain.port;

import java.util.Optional;

import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalAuthResponse;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalUserSearchRequest;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.UserResponse;
import reactor.core.publisher.Mono;

public interface UserClientPort {

    Mono<InternalAuthResponse> authenticate(String username, String password);
    Optional<UserResponse> getUser(InternalUserSearchRequest request);
    Optional<UserResponse> getUserById(String userId);
    void updatePassword(String userId, String newPassword);
}
