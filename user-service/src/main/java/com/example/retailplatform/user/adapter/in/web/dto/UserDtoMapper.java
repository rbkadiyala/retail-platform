package com.example.retailplatform.user.adapter.in.web.dto;

import com.example.retailplatform.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

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
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .role(user.getRole() != null ? user.getRole().name() : null)
                .active(user.getActive() != null ? user.getActive() : true)
                .build();
    }
}
