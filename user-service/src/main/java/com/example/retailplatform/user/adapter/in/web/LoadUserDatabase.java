    package com.example.retailplatform.user.adapter.in.web;

    import com.example.retailplatform.user.domain.model.Role;
    import com.example.retailplatform.user.domain.model.Status;
    import com.example.retailplatform.user.domain.model.User;
    import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
    import lombok.RequiredArgsConstructor;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.password.PasswordEncoder;

    @Configuration
    @RequiredArgsConstructor
    public class LoadUserDatabase {

        private final UserRepositoryPort userRepository;
        private final PasswordEncoder passwordEncoder;

        private static final Logger log = LoggerFactory.getLogger("com.example.retailplatform");

        @Value("${admin.username}")
        private String adminUsername;

        @Value("${admin.password}")
        private String adminPassword;

        @Value("${admin.email}")
        private String adminEmail;

        @Bean
        CommandLineRunner initAdminUser() {
            return args -> {
                log.info("Starting admin user initialization");
                log.debug("Admin username: {}, email: {}", adminUsername, adminEmail);

                var existingAdminOpt = userRepository.findActiveByUsername(adminUsername);

                if (existingAdminOpt.isPresent()) {
                    User adminUser = existingAdminOpt.get();
                    log.info("Existing admin found with ID: {}", adminUser.getId());
                    log.debug("Before update: {}", toLogString(adminUser));

                    // Update password and passwordChangeRequired
                    adminUser.setPassword(passwordEncoder.encode(adminPassword));
                    adminUser.setPasswordChangeRequired(true);
                    adminUser.setStatus(Status.ACTIVE);
                    adminUser.setRole(Role.ADMIN);

                    User updatedAdmin = userRepository.patch(adminUser);
                    log.debug("After update: {}", toLogString(updatedAdmin));
                    log.info("Admin user updated successfully: {}", adminUsername);

                } else {
                    User adminUser = User.builder()
                            .username(adminUsername)
                            .firstName("System")
                            .lastName("Admin")
                            .email(adminEmail)
                            .phoneNumber("0000000000")
                            .status(Status.ACTIVE)
                            .role(Role.ADMIN)
                            .password(passwordEncoder.encode(adminPassword))
                            .passwordChangeRequired(true)
                            .build();

                    log.debug("Creating new admin user: {}", toLogString(adminUser));
                    User savedAdmin = userRepository.save(adminUser);
                    log.debug("After save: {}", toLogString(savedAdmin));
                    log.info("Admin user created successfully: {}", adminUsername);
                }

                log.info("Admin user initialization completed");
            };
        }

        /** Helper method to log user fields excluding password **/
        private String toLogString(User user) {
            if (user == null) return "null";
            return String.format(
                    "id=%s, username=%s, firstName=%s, lastName=%s, email=%s, phoneNumber=%s, status=%s, role=%s, active=%s, passwordChangeRequired=%s",
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getStatus(),
                    user.getRole(),
                    user.getActive(),
                    user.getPasswordChangeRequired()
            );
        }
    }
