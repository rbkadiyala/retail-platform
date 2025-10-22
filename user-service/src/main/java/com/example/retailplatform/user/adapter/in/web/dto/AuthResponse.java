package com.example.retailplatform.user.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private final boolean authenticated;
    private final JwtUserResponse user;
}
