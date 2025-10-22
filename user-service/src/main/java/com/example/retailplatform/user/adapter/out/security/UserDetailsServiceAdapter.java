package com.example.retailplatform.user.adapter.out.security;

import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceAdapter implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceAdapter.class);

    private final UserRepositoryPort userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Attempting to load user: {}", identifier);

        Optional<User> userOpt = resolveUser(identifier);

        User user = userOpt.orElseThrow(() -> {
            log.warn("User not found for identifier: {}", identifier);
            return new UsernameNotFoundException("User not found: " + identifier);
        });
        
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(), // hashed password
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                "ACTIVE".equalsIgnoreCase(user.getStatus().name())
        );
    }

    private Optional<User> resolveUser(String identifier) {
        if (identifier.contains("@")) {
            // Looks like email
            return userRepository.findActiveByEmail(identifier);
        } else if (identifier.matches("\\d+")) {
            // Looks like phone number (digits only)
            return userRepository.findActiveByPhoneNumber(identifier);
        } else {
            // Default to username
            return userRepository.findActiveByUsername(identifier);
        }
    }


    public static class CustomUserDetails implements UserDetails {
        private final String username;
        private final String password;
        private final Collection<? extends GrantedAuthority> authorities;
        private final boolean enabled;

        public CustomUserDetails(String username, String password,
                                Collection<? extends GrantedAuthority> authorities, boolean enabled) {
            this.username = username;
            this.password = password;
            this.authorities = authorities;
            this.enabled = enabled;
        }

        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
        @Override public String getPassword() { return password; }
        @Override public String getUsername() { return username; }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return enabled; }
    }
}
