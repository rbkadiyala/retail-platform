package com.example.retailplatform.user.application.service;

import com.example.retailplatform.user.adapter.in.web.dto.AuthRequest;
import com.example.retailplatform.user.adapter.in.web.dto.AuthResponse;
import com.example.retailplatform.user.adapter.in.web.dto.JwtUserResponse;
import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort repositoryPort;
    private final AuthenticationManager authenticationManager;

    // ------------------- Core MeUserServicethods -------------------

    @Override
    public User createUser(User user) {
        if (user.getStatus() == null) user.setStatus(Status.INACTIVE);
        if (user.getRole() == null) user.setRole(Role.USER);
        if (user.getActive() == null) user.setActive(true);

        ensureUniqueGlobal(user.getUsername(), UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY);
        ensureUniqueActive(user.getEmail(), UserConstants.FIELD_EMAIL, repositoryPort::existsByActiveEmail, UserConstants.EMAIL_ALREADY_EXISTS_KEY);
        ensureUniqueActive(user.getPhoneNumber(), UserConstants.FIELD_PHONE, repositoryPort::existsByActivePhoneNumber, UserConstants.PHONE_ALREADY_EXISTS_KEY);

        return repositoryPort.save(user);
    }

    @Override
    public User getUserById(String id) {
        return repositoryPort.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", id, UserConstants.USER_NOT_FOUND_KEY
                ));
    }

    @Override
    public List<User> getAllUsers() {
        return repositoryPort.findAllActive();
    }

    @Override
    public User updateUser(String id, User update) {
        User existing = getUserById(id);
        update.setId(id);

        ensureUniqueGlobalForUpdate(update.getUsername(), existing.getId(), UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY);
        ensureUniqueActiveForUpdate(update.getEmail(), existing.getId(), repositoryPort::findActiveByEmail, UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY);
        ensureUniqueActiveForUpdate(update.getPhoneNumber(), existing.getId(), repositoryPort::findActiveByPhoneNumber, UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY);

        copyFields(existing, update);

        return repositoryPort.patch(existing);
    }

    @Override
    public User patchUser(String id, User patch) {
        User existing = getUserById(id);
        boolean changed = false;

        changed |= patchField(existing::getUsername, existing::setUsername, patch.getUsername(),
                val -> ensureUniqueGlobalForUpdate(val, existing.getId(), UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY));
        changed |= patchField(existing::getEmail, existing::setEmail, patch.getEmail(),
                val -> ensureUniqueActiveForUpdate(val, existing.getId(), repositoryPort::findActiveByEmail, UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY));
        changed |= patchField(existing::getPhoneNumber, existing::setPhoneNumber, patch.getPhoneNumber(),
                val -> ensureUniqueActiveForUpdate(val, existing.getId(), repositoryPort::findActiveByPhoneNumber, UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY));
        changed |= patchField(existing::getFirstName, existing::setFirstName, patch.getFirstName(), null);
        changed |= patchField(existing::getLastName, existing::setLastName, patch.getLastName(), null);
        changed |= patchField(existing::getStatus, existing::setStatus, patch.getStatus(), null);
        changed |= patchField(existing::getRole, existing::setRole, patch.getRole(), null);
        changed |= patchField(existing::getActive, existing::setActive, patch.getActive(), null);

        return changed ? repositoryPort.patch(existing) : existing;
    }

    @Override
    public void softDeleteUser(String id) {
        User existing = repositoryPort.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", id, UserConstants.USER_NOT_FOUND_KEY
                ));

        existing.setActive(false);
        repositoryPort.patch(existing);
    }

    public AuthResponse authenticate(AuthRequest request) {
        String identifier = getIdentifier(request);
        if (identifier == null) {
            return AuthResponse.builder().authenticated(false).build();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, request.getPassword())
            );

            Optional<User> userOpt = findUser(request);
            if (userOpt.isEmpty()) {
                return AuthResponse.builder().authenticated(false).build();
            }

            User user = userOpt.get();
            JwtUserResponse jwtUser = JwtUserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole().name())
                    .passwordChangeRequired(user.getPasswordChangeRequired())
                    .build();

            return AuthResponse.builder()
                    .authenticated(true)
                    .user(jwtUser)
                    .build();

        } catch (Exception e) {
            //log.warn("Authentication failed for identifier {}: {}", identifier, e.getMessage());
            return AuthResponse.builder().authenticated(false).build();
        }
    }

    
    // ------------------- Search Method -------------------
    @Override
    public List<User> searchUsers(String username, String email, String phoneNumber) {
        return repositoryPort.searchActiveUsers(username, email, phoneNumber);
    }

    // ------------------- Helper Methods -------------------

    private void ensureUniqueGlobal(String value, String fieldName, String key) {
        if (value != null && repositoryPort.existsByUsername(value)) {
            throw new ResourceAlreadyExistsException("User", fieldName, value, key);
        }
    }

    private void ensureUniqueActive(String value, String fieldName, Predicate<String> existsChecker, String key) {
        if (value != null && existsChecker.test(value)) {
            throw new ResourceAlreadyExistsException("User", fieldName, value, key);
        }
    }

    private void ensureUniqueGlobalForUpdate(String value, String currentUserId, String fieldName, String key) {
        if (value != null) {
            repositoryPort.findActiveByUsername(value)
                    .filter(u -> !u.getId().equals(currentUserId))
                    .ifPresent(u -> { throw new ResourceAlreadyExistsException("User", fieldName, value, key); });
        }
    }

    private void ensureUniqueActiveForUpdate(String value, String currentUserId,
                                             Function<String, Optional<User>> finder,
                                             String fieldName, String key) {
        if (value != null) {
            finder.apply(value)
                    .filter(u -> !u.getId().equals(currentUserId))
                    .ifPresent(u -> { throw new ResourceAlreadyExistsException("User", fieldName, value, key); });
        }
    }

    private void copyFields(User existing, User update) {
        existing.setUsername(update.getUsername());
        existing.setEmail(update.getEmail());
        existing.setPhoneNumber(update.getPhoneNumber());
        existing.setFirstName(update.getFirstName() != null ? update.getFirstName() : existing.getFirstName());
        existing.setLastName(update.getLastName() != null ? update.getLastName() : existing.getLastName());
        existing.setStatus(update.getStatus() != null ? update.getStatus() : existing.getStatus());
        existing.setRole(update.getRole() != null ? update.getRole() : existing.getRole());
        existing.setActive(update.getActive() != null ? update.getActive() : existing.getActive());
    }

    private <T> boolean patchField(java.util.function.Supplier<T> getter,
                                   java.util.function.Consumer<T> setter,
                                   T newValue,
                                   java.util.function.Consumer<T> uniquenessCheck) {
        if (newValue != null && !newValue.equals(getter.get())) {
            if (uniquenessCheck != null) uniquenessCheck.accept(newValue);
            setter.accept(newValue);
            return true;
        }
        return false;
    }

     private String getIdentifier(AuthRequest request) {
        if (request.getUsername() != null && !request.getUsername().isBlank()) return request.getUsername();
        if (request.getEmail() != null && !request.getEmail().isBlank()) return request.getEmail();
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) return request.getPhoneNumber();
        return null;
    }

    private Optional<User> findUser(AuthRequest request) {
        if (request.getUsername() != null) return repositoryPort.findActiveByUsername(request.getUsername());
        if (request.getEmail() != null) return repositoryPort.findActiveByEmail(request.getEmail());
        if (request.getPhoneNumber() != null) return repositoryPort.findActiveByPhoneNumber(request.getPhoneNumber());
        return Optional.empty();
    }
}
