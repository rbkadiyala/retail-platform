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

@Configuration
@RequiredArgsConstructor
public class LoadUserDatabase {

    private final UserRepositoryPort userRepository;

    /**
     * Always ensures a fallback admin user exists (in all environments).
     */
    @Bean
    CommandLineRunner initAdminUser() {
        return args -> userRepository.findActiveByUsername("admin")
            .orElseGet(() -> {
                User admin = new User(
                        null,
                        "System",
                        "Admin",
                        "admin",
                        "admin@example.com",
                        "0000000000",
                        Status.ACTIVE,
                        Role.ADMIN,
                        true
                );
                return userRepository.save(admin);
            });
    }

    /**
     * Creates predefined demo users — runs only in 'dev' or 'test' profiles.
     */
    @Bean
    @Profile({"dev", "test"})
    CommandLineRunner initDemoUsers() {
        return args -> {
            createUserIfNotExists("alice.smith", "Alice", "Smith", "alice@example.com", "1234567890", Role.USER);
            createUserIfNotExists("bob.johnson", "Bob", "Johnson", "bob@example.com", "0987654321", Role.ADMIN);
            createUserIfNotExists("carol.williams", "Carol", "Williams", "carol@example.com", "1112223333", Role.ADMIN);
        };
    }

    private void createUserIfNotExists(String username,
                                       String firstName,
                                       String lastName,
                                       String email,
                                       String phoneNumber,
                                       Role role) {
        userRepository.findActiveByUsername(username)
            .orElseGet(() -> {
                User user = new User(
                        null,
                        firstName,
                        lastName,
                        username,
                        email,
                        phoneNumber,
                        Status.ACTIVE,
                        role,
                        true
                );
                return userRepository.save(user);
            });
    }
}
