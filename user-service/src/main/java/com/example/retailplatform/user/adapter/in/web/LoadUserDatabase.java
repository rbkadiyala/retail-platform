package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class LoadUserDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadUserDatabase.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder; // Injected via constructor

    @Bean
    CommandLineRunner initAdminUser() {
        return args -> {
            userRepository.findActiveByUsername("admin").ifPresentOrElse(
                existing -> log.info("Admin user '{}' already exists", existing.getUsername()),
                () -> createUser("admin", "System", "Admin", "admin@example.com", "0000000000", Role.ADMIN, "admin123")
            );
        };
    }

    @Bean
    @Profile({"dev"})
    CommandLineRunner initDemoUsers() {
        return args -> {
            createUserIfNotExists("alice.smith", "Alice", "Smith", "alice@example.com", "1234567890", Role.USER, "alice123");
            createUserIfNotExists("bob.johnson", "Bob", "Johnson", "bob@example.com", "0987654321", Role.ADMIN, "bob123");
            createUserIfNotExists("carol.williams", "Carol", "Williams", "carol@example.com", "1112223333", Role.ADMIN, "carol123");
        };
    }

    private void createUserIfNotExists(String username,
                                       String firstName,
                                       String lastName,
                                       String email,
                                       String phoneNumber,
                                       Role role,
                                       String rawPassword) {

        userRepository.findActiveByUsername(username).ifPresentOrElse(
            existing -> log.info("User '{}' already exists", existing.getUsername()),
            () -> createUser(username, firstName, lastName, email, phoneNumber, role, rawPassword)
        );
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
                passwordEncoder.encode(rawPassword) // Now works
        );
        userRepository.save(user);
        
    }
}
