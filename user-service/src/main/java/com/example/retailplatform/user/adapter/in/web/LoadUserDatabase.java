package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class LoadUserDatabase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder; // Injected via constructor

    @Bean
    CommandLineRunner initAdminUser() {
        return args -> {
            // Directly create admin user without checking
            createUser("admin", "System", "Admin", "admin@example.com", "0000000000", Role.ADMIN, "admin123");
        };
    }

    private void createUser(String username,
                            String firstName,
                            String lastName,
                            String email,
                            String phoneNumber,
                            Role role,
                            String rawPassword) {
        User user = new User(
                null,
                firstName,
                lastName,
                username,
                email,
                phoneNumber,
                Status.ACTIVE,
                role,
                true,
                passwordEncoder.encode(rawPassword)
        );
        userRepository.save(user);
    }
}
