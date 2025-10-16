package com.example.retailplatform.user.adapter.out.persistence;

import com.example.retailplatform.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public User toModel(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .status(entity.getStatus())
                .role(entity.getRole())
                .active(!entity.isDeleted()) // convert deleted -> active
                .password(entity.getPassword())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity.UserEntityBuilder builder = UserEntity.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .role(user.getRole())
                .password(user.getPassword()); // <- add this

        if (user.getActive() != null) {
            builder.deleted(!user.getActive());
        }

        if (user.getId() != null) {
            try {
                builder.id(Long.parseLong(user.getId()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid user ID: " + user.getId());
            }
        }

        return builder.build();
    }

    /**
     * Apply non-null fields from domain User into existing entity (for PATCH).
     * Does NOT overwrite createdAt or createdBy.
     */
    public void updateEntityFromModel(User user, UserEntity entity) {
        if (user.getFirstName() != null) entity.setFirstName(user.getFirstName());
        if (user.getLastName() != null) entity.setLastName(user.getLastName());
        if (user.getUsername() != null) entity.setUsername(user.getUsername());
        if (user.getEmail() != null) entity.setEmail(user.getEmail());
        if (user.getPhoneNumber() != null) entity.setPhoneNumber(user.getPhoneNumber());
        if (user.getStatus() != null) entity.setStatus(user.getStatus());
        if (user.getRole() != null) entity.setRole(user.getRole());
        if (user.getActive() != null) entity.setDeleted(!user.getActive());
        
        // Update password if provided
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            entity.setPassword(user.getPassword());
        }
    }
}
