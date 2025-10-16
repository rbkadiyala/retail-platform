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

@Service
@RequiredArgsConstructor
public class UserDetailsServiceAdapter implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceAdapter.class);

    private final UserRepositoryPort userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user: {}", username);

        User user = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User {} not found or cannot authenticate locally", username);
                    return new UsernameNotFoundException(
                            "User not found or cannot authenticate locally: " + username);
                });

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(), // hashed password
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                "ACTIVE".equalsIgnoreCase(user.getStatus().name())
        );
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
