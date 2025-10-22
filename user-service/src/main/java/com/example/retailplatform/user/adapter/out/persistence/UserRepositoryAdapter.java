package com.example.retailplatform.user.adapter.out.persistence;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper entityMapper;

    @Override
    public User save(User user) {
        // Check uniqueness before saving
        checkUniqueFields(user);

        UserEntity entity = entityMapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return entityMapper.toModel(saved);
    }

    @Override
    public List<User> findAllActive() {
        return jpaRepository.findAllActive()
                .stream()
                .map(entityMapper::toModel)
                .toList();
    }

    @Override
    public Optional<User> findActiveById(String id) {
        return jpaRepository.findActiveById(parseId(id))
                .map(entityMapper::toModel);
    }

    @Override
    public Optional<User> findActiveByUsername(String username) {
        return jpaRepository.findActiveByUsername(username)
                .map(entityMapper::toModel);
    }

    @Override
    public Optional<User> findActiveByEmail(String email) {
        return jpaRepository.findActiveByEmail(email)
                .map(entityMapper::toModel);
    }

    @Override
    public Optional<User> findActiveByPhoneNumber(String phoneNumber) {
        return jpaRepository.findActiveByPhoneNumber(phoneNumber)
                .map(entityMapper::toModel);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByActiveEmail(String email) {
        return jpaRepository.existsByActiveEmail(email);
    }

    @Override
    public boolean existsByActivePhoneNumber(String phoneNumber) {
        return jpaRepository.existsByActivePhoneNumber(phoneNumber);
    }

    @Override
    public User patch(User user) {
        Long pk = parseId(user.getId());
        UserEntity entity = jpaRepository.findActiveById(pk)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User",                   // resourceName
                        user.getId(),             // resourceId
                        UserConstants.USER_NOT_FOUND_KEY // messageKey
                ));

        // Check uniqueness for fields being updated
        checkUniqueFields(user, entity.getId());

        entityMapper.updateEntityFromModel(user, entity);
        UserEntity saved = jpaRepository.save(entity);
        return entityMapper.toModel(saved);
    }

    @Override
    public void softDelete(String id) {
        Long pk = parseId(id);
        jpaRepository.findActiveById(pk).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }

    @Override
    public List<User> searchActiveUsers(String username, String email, String phoneNumber) {
        // Delegate to JPA repository method, mapping entities to domain models
        return jpaRepository.searchActiveUsers(username, email, phoneNumber)
                .stream()
                .map(entityMapper::toModel)
                .toList();
    }

    // ------------------ Helper Methods ------------------

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }

    /**
     * Checks for uniqueness before creating a new user.
     */
    private void checkUniqueFields(User user) {
        checkUsername(user.getUsername(), null);
        checkEmail(user.getEmail(), null);
        checkPhone(user.getPhoneNumber(), null);
    }

    /**
     * Checks for uniqueness before updating an existing user.
     * Skips the current user's own record using currentUserId.
     */
    private void checkUniqueFields(User user, Long currentUserId) {
        checkUsername(user.getUsername(), currentUserId);
        checkEmail(user.getEmail(), currentUserId);
        checkPhone(user.getPhoneNumber(), currentUserId);
    }

    private void checkUsername(String username, Long currentUserId) {
        Optional<UserEntity> existing = jpaRepository.findActiveByUsername(username);
        if (existing.isPresent() && !existing.get().getId().equals(currentUserId)) {
            throw new ResourceAlreadyExistsException(
                    "User",
                    UserConstants.FIELD_USERNAME,
                    username,
                    UserConstants.USERNAME_ALREADY_EXISTS_KEY
            );
        }
    }

    private void checkEmail(String email, Long currentUserId) {
        Optional<UserEntity> existing = jpaRepository.findActiveByEmail(email);
        if (existing.isPresent() && !existing.get().getId().equals(currentUserId)) {
            throw new ResourceAlreadyExistsException(
                    "User",
                    UserConstants.FIELD_EMAIL,
                    email,
                    UserConstants.EMAIL_ALREADY_EXISTS_KEY
            );
        }
    }

    private void checkPhone(String phoneNumber, Long currentUserId) {
        Optional<UserEntity> existing = jpaRepository.findActiveByPhoneNumber(phoneNumber);
        if (existing.isPresent() && !existing.get().getId().equals(currentUserId)) {
            throw new ResourceAlreadyExistsException(
                    "User",
                    UserConstants.FIELD_PHONE,
                    phoneNumber,
                    UserConstants.PHONE_ALREADY_EXISTS_KEY
            );
        }
    }
}
