package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;
    private String refreshToken; // optional, can return same or new token
}
