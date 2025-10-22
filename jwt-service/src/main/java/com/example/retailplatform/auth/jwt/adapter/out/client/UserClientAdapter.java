package com.example.retailplatform.auth.jwt.adapter.out.client;

import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalAuthRequest;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalAuthResponse;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.InternalUserSearchRequest;
import com.example.retailplatform.auth.jwt.adapter.in.web.dto.UserResponse;
import com.example.retailplatform.auth.jwt.domain.port.UserClientPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserClientPort {

    private static final Logger log = LoggerFactory.getLogger(UserClientAdapter.class);
    private final WebClient userServiceWebClient;

    @Override
    public Mono<InternalAuthResponse> authenticate(String username, String password) {
        InternalAuthRequest request = InternalAuthRequest.builder()
                .username(username)
                .password(password)
                .build();

        return userServiceWebClient.post()
                .uri("/api/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InternalAuthResponse.class)
                .doOnError(e -> log.error("Error calling User Service authenticate: {}", e.getMessage()));
    }

    @Override
    public Optional<UserResponse> getUser(InternalUserSearchRequest request) {
        try {
            UserResponse[] users = userServiceWebClient.post()
                    .uri("/api/users/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(UserResponse[].class)
                    .block();

            if (users != null && users.length > 0) {
                return Optional.of(users[0]);
            }
        } catch (WebClientResponseException e) {
            log.error("Error searching user: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Unexpected error searching user: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserResponse> getUserById(String userId) {
        try {
            UserResponse user = userServiceWebClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();

            return Optional.ofNullable(user);
        } catch (WebClientResponseException e) {
            log.error("Error fetching user by ID {}: {} - {}", userId, e.getStatusCode().value(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching user by ID {}: {}", userId, e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void updatePassword(String userId, String newPassword) {
        try {
            userServiceWebClient.put()
                    .uri("/api/users/password-reset/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new PasswordUpdateRequest(userId, newPassword))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error updating password: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Unexpected error updating password: {}", e.getMessage(), e);
        }
    }

    // Minimal DTO for password update
    private record PasswordUpdateRequest(String userId, String newPassword) {}
}
