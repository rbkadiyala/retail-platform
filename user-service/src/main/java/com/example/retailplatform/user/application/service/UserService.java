package com.example.retailplatform.user.application.service;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort repositoryPort;

    // ------------------- Core Methods -------------------

    @Override
    public User createUser(User user) {
        // Set defaults
        if (user.getStatus() == null) user.setStatus(Status.INACTIVE);
        if (user.getRole() == null) user.setRole(Role.USER);
        if (user.getActive() == null) user.setActive(true);

        // Uniqueness checks
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

        // Update uniqueness checks
        ensureUniqueGlobalForUpdate(update.getUsername(), existing.getId(), UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY);
        ensureUniqueActiveForUpdate(update.getEmail(), existing.getId(), repositoryPort::findActiveByEmail, UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY);
        ensureUniqueActiveForUpdate(update.getPhoneNumber(), existing.getId(), repositoryPort::findActiveByPhoneNumber, UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY);

        // Update fields
        copyFields(existing, update);

        return repositoryPort.patch(existing);
    }

    @Override
    public User patchUser(String id, User patch) {
        User existing = getUserById(id);
        boolean changed = false;

        // Patch fields with uniqueness
        changed |= patchField(existing::getUsername, existing::setUsername, patch.getUsername(),
                val -> ensureUniqueGlobalForUpdate(val, existing.getId(), UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY));

        changed |= patchField(existing::getEmail, existing::setEmail, patch.getEmail(),
                val -> ensureUniqueActiveForUpdate(val, existing.getId(), repositoryPort::findActiveByEmail,
                        UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY));

        changed |= patchField(existing::getPhoneNumber, existing::setPhoneNumber, patch.getPhoneNumber(),
                val -> ensureUniqueActiveForUpdate(val, existing.getId(), repositoryPort::findActiveByPhoneNumber,
                        UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY));

        changed |= patchField(existing::getFirstName, existing::setFirstName, patch.getFirstName(), null);
        changed |= patchField(existing::getLastName, existing::setLastName, patch.getLastName(), null);
        changed |= patchField(existing::getStatus, existing::setStatus, patch.getStatus(), null);
        changed |= patchField(existing::getRole, existing::setRole, patch.getRole(), null);
        changed |= patchField(existing::getActive, existing::setActive, patch.getActive(), null);

        return changed ? repositoryPort.patch(existing) : existing;
    }

    @Override
    public void softDeleteUser(String id) {
        repositoryPort.softDelete(id); // only active users are affected
    }

    // ------------------- Helper Methods -------------------

    // --- Uniqueness checks ---

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
                    .ifPresent(u -> {
                        throw new ResourceAlreadyExistsException("User", fieldName, value, key);
                    });
        }
    }

    private void ensureUniqueActiveForUpdate(String value, String currentUserId,
                                             Function<String, Optional<User>> finder,
                                             String fieldName, String key) {
        if (value != null) {
            finder.apply(value)
                    .filter(u -> !u.getId().equals(currentUserId))
                    .ifPresent(u -> {
                        throw new ResourceAlreadyExistsException("User", fieldName, value, key);
                    });
        }
    }

    // --- Field copying ---
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

    // --- Generic patch helper ---
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
}

