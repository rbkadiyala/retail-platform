package com.example.retailplatform.user.adapter.out.persistence;

import com.example.retailplatform.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    private static final Logger log = LoggerFactory.getLogger(UserEntityMapper.class);

    public User toModel(UserEntity entity) {
        if (entity == null) return null;

        log.debug("toModel - entity: {}", toLogString(entity));

        User user = User.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .status(entity.getStatus())
                .role(entity.getRole())
                .password(entity.getPassword()) // <-- Add this line to set password
                .active(entity.isActive())
                .passwordChangeRequired(entity.isPasswordChangeRequired())
                .build();

        log.debug("toModel - mapped user: {}", toLogString(user));
        return user;
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity entity = UserEntity.builder()
                .id(parseId(user.getId()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .role(user.getRole())
                .active(defaultTrue(user.getActive()))
                .password(user.getPassword())
                .passwordChangeRequired(defaultTrue(user.getPasswordChangeRequired()))
                .build();

        log.debug("toEntity - mapped entity: {}", toLogString(entity));
        return entity;
    }

    public void updateEntityFromModel(User user, UserEntity entity) {
        log.debug("updateEntityFromModel - before update: {}", toLogString(entity));

        copyIfNotNull(user.getFirstName(), entity::setFirstName);
        copyIfNotNull(user.getLastName(), entity::setLastName);
        copyIfNotNull(user.getUsername(), entity::setUsername);
        copyIfNotNull(user.getEmail(), entity::setEmail);
        copyIfNotNull(user.getPhoneNumber(), entity::setPhoneNumber);
        copyIfNotNull(user.getStatus(), entity::setStatus);
        copyIfNotNull(user.getRole(), entity::setRole);
        copyIfNotNull(user.getActive(), entity::setActive);

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            entity.setPassword(user.getPassword());
        }

        entity.setPasswordChangeRequired(user.getPasswordChangeRequired() != null ? user.getPasswordChangeRequired() : true);

        log.debug("updateEntityFromModel - after update: {}", toLogString(entity));
    }

    private Long parseId(String id) {
        try {
            return id != null ? Long.parseLong(id) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean defaultTrue(Boolean value) {
        return value != null ? value : true;
    }

    private <T> void copyIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private String toLogString(UserEntity entity) {
        if (entity == null) return "null";
        return String.format("id=%s, username=%s, firstName=%s, lastName=%s, email=%s, phone=%s, status=%s, role=%s, active=%s, passwordChangeRequired=%s",
                entity.getId(), entity.getUsername(), entity.getFirstName(), entity.getLastName(), entity.getEmail(),
                entity.getPhoneNumber(), entity.getStatus(), entity.getRole(), entity.isActive(), entity.isPasswordChangeRequired());
    }

    private String toLogString(User user) {
        if (user == null) return "null";
        return String.format("id=%s, username=%s, firstName=%s, lastName=%s, email=%s, phone=%s, status=%s, role=%s, active=%s, passwordChangeRequired=%s",
                user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), user.getStatus(), user.getRole(), user.getActive(), user.getPasswordChangeRequired());
    }
}
