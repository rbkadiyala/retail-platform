package com.example.retailplatform.auth.jwt.security;

import com.example.retailplatform.auth.jwt.client.UserClient;
import com.example.retailplatform.auth.jwt.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = userClient.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Use "password" field from UserDto
        String password = user.getPassword();  

        // Default to role "USER" if null
        String role = user.getRole() != null ? user.getRole() : "USER";

        return User.builder()
                .username(user.getUsername())
                .password(password)  // password should be hashed in user-service
                .roles(role)
                .build();
    }
}
