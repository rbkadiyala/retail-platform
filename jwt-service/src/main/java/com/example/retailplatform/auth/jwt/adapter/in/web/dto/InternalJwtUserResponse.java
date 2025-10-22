package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalJwtUserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private boolean passwordChangeRequired;
}    

