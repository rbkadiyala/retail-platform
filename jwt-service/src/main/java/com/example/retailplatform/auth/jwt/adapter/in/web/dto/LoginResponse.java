package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after successful authentication")
public class LoginResponse {

    @Schema(description = "JWT access token used for API authentication", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

    @Schema(description = "Optional refresh token used to obtain new access tokens", example = "def50200a7...")
    private String refreshToken;

    @Schema(description = "Token type, usually 'Bearer'", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access token expiration time in seconds", example = "3600")
    private long expiresIn;

    @Schema(description = "Minimal user information attached to the token")
    private InternalJwtUserResponse user;
}
