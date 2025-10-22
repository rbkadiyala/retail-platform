package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "User ID must not be blank")
    private String userId;

    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;
}
