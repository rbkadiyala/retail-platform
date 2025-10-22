package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternalPasswordResetRequest {
    private String username;      // optional
    private String email;         // optional
    private String phoneNumber;   // optional

    /**
     * Ensure at least one identifier is present.
     */
    public boolean hasIdentifier() {
        return (username != null && !username.isBlank()) ||
               (email != null && !email.isBlank()) ||
               (phoneNumber != null && !phoneNumber.isBlank());
    }
}
