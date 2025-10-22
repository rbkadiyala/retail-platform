package com.example.retailplatform.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;


@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Relation(collectionRelation = "userList", itemRelation = "user")

public class UserResponse extends RepresentationModel<UserResponse> {

    @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "First name of the user", example = "Alice")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Smith")
    private String lastName;

    @Schema(description = "Username of the user", example = "alice.smith")
    private String username;

    @Schema(description = "Email address of the user", example = "alice@example.com")
    private String email;

    @Schema(description = "Phone number of the user", example = "1234567890")
    private String phoneNumber;

    @Schema(description = "Status of the user", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
    private String status;

    @Schema(description = "Role of the user", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;

    @Schema(description = "Indicates if the user account is active",
            example = "true",
            defaultValue = "false")
    private boolean active;  // primitive -> clients never see null

    @Schema(description = "Whether password change is required", example = "true")
    private Boolean passwordChangeRequired;
}
