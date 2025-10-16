package com.example.retailplatform.user.config;

import com.example.retailplatform.user.common.ErrorResponse;
import com.example.retailplatform.user.domain.UserConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public SecurityConfig(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/actuator/health",
                        "/actuator/info",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                String message = messageSource.getMessage(
                        UserConstants.ERROR_AUTH_FAILED, null, "Authentication failed", request.getLocale()
                );
                ErrorResponse errorResponse = ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "AUTHENTICATION_FAILED",
                        UserConstants.ERROR_AUTH_FAILED,
                        request.getRequestURI(),
                        message,
                        null,
                        List.of()
                );
                writeErrorResponse(response, errorResponse);
            }))
            .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, accessDeniedException) -> {
                String message = messageSource.getMessage(
                        UserConstants.ERROR_ACCESS_DENIED, null, "Access denied", request.getLocale()
                );
                ErrorResponse errorResponse = ErrorResponse.of(
                        HttpStatus.FORBIDDEN.value(),
                        "ACCESS_DENIED",
                        UserConstants.ERROR_ACCESS_DENIED,
                        request.getRequestURI(),
                        message,
                        null,
                        List.of()
                );
                writeErrorResponse(response, errorResponse);
            }));

        return http.build();
    }

    // ------------------ Helper ------------------
    private void writeErrorResponse(HttpServletResponse response,
                                    ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getStatus());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
