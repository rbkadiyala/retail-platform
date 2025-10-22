package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PasswordResetTokenResponse {

    private String userId;
    private String token;
    private LocalDateTime expiresAt;
}
