package com.example.retailplatform.user.adapter.in.web.dto;

import com.example.retailplatform.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    /**
     * Map UserRequest DTO to User domain model
     */
    public User toModel(UserRequest request) {
        if (request == null) return null;

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .status(request.getStatus())
                .role(request.getRole())
                .active(request.getActive())
                .passwordChangeRequired(request.getPasswordChangeRequired())
                .build();
    }

    /**
     * Map User domain model to UserResponse DTO
     * Safely converts nullable Boolean fields to primitive boolean
     */
    public UserResponse toResponse(User user) {
        if (user == null) return null;

        // Safe Boolean -> boolean conversion
        boolean isActive = user.getActive() != null && user.getActive();
        boolean isPwdChangeRequired = user.getPasswordChangeRequired() != null && user.getPasswordChangeRequired();

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(enumName(user.getStatus()))
                .role(enumName(user.getRole()))                
                .active(isActive)
                .passwordChangeRequired(isPwdChangeRequired)
                .build();
    }

    /** Helper to safely get enum name or null */
    private String enumName(Enum<?> e) {
        return e != null ? e.name() : null;
    }
}
