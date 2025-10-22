package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String status;
    private String role;
    private boolean active;
    private boolean passwordChangeRequired;

    public boolean isAuthenticated() {
        return id != null;
    }
}
