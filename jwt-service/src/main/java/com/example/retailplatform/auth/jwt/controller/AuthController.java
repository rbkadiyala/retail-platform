package com.example.retailplatform.auth.jwt.controller;

import com.example.retailplatform.auth.jwt.dto.AuthRequest;
import com.example.retailplatform.auth.jwt.dto.AuthResponse;
import com.example.retailplatform.auth.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate user and return JWT token.
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return new AuthResponse(token);
    }
}
