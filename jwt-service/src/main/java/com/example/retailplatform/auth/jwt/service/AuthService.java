package com.example.retailplatform.auth.jwt.service;

import com.example.retailplatform.auth.jwt.client.UserClient;
import com.example.retailplatform.auth.jwt.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String authenticate(String username, String password) {
        var user = userClient.getUserByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
    }

    public void register(String username, String rawPassword, String role) {
        // 1. Check if user exists in user-service
        var userProfile = userClient.getUserByUsername(username);
        if (userProfile == null) {
            throw new RuntimeException("Cannot register credentials: user does not exist in user-service");
        }

        // 2. Check if credentials already exist in jwt-service
        if (authUserRepository.existsById(username)) {
            throw new RuntimeException("User credentials already exist");
        }

        // 3. Hash password
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // 4. Save credentials
        AuthUser user = AuthUser.builder()
                .username(username)
                .password(hashedPassword)
                .role(role)
                .build();

        authUserRepository.save(user);
    }

}
