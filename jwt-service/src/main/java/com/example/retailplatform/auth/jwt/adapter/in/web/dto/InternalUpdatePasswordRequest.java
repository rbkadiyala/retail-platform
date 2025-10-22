package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternalUpdatePasswordRequest {
    private String userId;        // required
    private String newPassword;   // required
    private String token;         // optional, for password reset flow

    /**
     * Validate required fields.
     */
    public boolean isValid() {
        return userId != null && !userId.isBlank() &&
               newPassword != null && !newPassword.isBlank();
    }
}
