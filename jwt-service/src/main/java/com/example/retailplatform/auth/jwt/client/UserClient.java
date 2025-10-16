package com.example.retailplatform.auth.jwt.client;

import com.example.retailplatform.auth.jwt.dto.UserDto;
import com.example.retailplatform.auth.jwt.dto.UserListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserDto getUserByUsername(String username) {
        UserListResponse response = webClientBuilder
                .baseUrl(userServiceUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users")
                        .queryParam("username", username)
                        .build())
                .retrieve()
                .bodyToMono(UserListResponse.class)
                .block();

        if (response != null && response.get_embedded() != null
                && !response.get_embedded().getUserList().isEmpty()) {
            return response.get_embedded().getUserList().get(0);
        }

        return null;
    }
}
