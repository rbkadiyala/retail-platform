package com.example.retailplatform.user.adapter.out.persistence;

import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;

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
        UserEntity entity = entityMapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return entityMapper.toModel(saved);
    }

    @Override
    public List<User> findAllActive() {
        return jpaRepository.findAllActive().stream()
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
                .orElseThrow(() -> new ResourceNotFoundException("Active user not found: " + user.getId()));

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

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }
}


