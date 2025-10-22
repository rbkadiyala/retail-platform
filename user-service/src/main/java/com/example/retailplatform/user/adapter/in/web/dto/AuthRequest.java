package com.example.retailplatform.user.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
}