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

    @Configuration
    @RequiredArgsConstructor
    public class LoadUserDatabase {

        private static final Logger log = LoggerFactory.getLogger(LoadUserDatabase.class);
        private final UserRepositoryPort userRepository;

        /**
         * Always ensures a fallback admin user exists (in all environments).
         */
        @Bean
        CommandLineRunner initAdminUser() {
            return args -> {
                userRepository.findActiveByUsername("admin").ifPresentOrElse(
                    existing -> log.info("Admin already exists: {}", existing),
                    () -> {
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
                        User savedAdmin = userRepository.save(admin);
                        log.info("Created fallback admin user: {}", savedAdmin);
                    }
                );
            };
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

                userRepository.findAllActive().forEach(user -> log.info("Preloaded user: {}", user));
            };
        }

        private void createUserIfNotExists(String username,
                                        String firstName,
                                        String lastName,
                                        String email,
                                        String phoneNumber,
                                        Role role) {
            userRepository.findActiveByUsername(username).ifPresentOrElse(
                existing -> log.info("User already exists: {}", existing),
                () -> {
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
                    User saved = userRepository.save(user);
                    log.info("Created default user: {}", saved);
                }
            );
        }
    }
