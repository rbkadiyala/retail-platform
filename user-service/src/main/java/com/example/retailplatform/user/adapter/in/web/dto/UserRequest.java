package com.example.retailplatform.user.adapter.in.web.dto;

import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50)
    @Schema(description = "First name of the user", example = "Alice")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50)
    @Schema(description = "Last name of the user", example = "Smith")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20)
    @Schema(description = "Username of the user", example = "alice.smith")
    private String username;

    @NotBlank(message = "Email is required")
    @Email
    @Size(min = 5, max = 100)
    @Schema(description = "Email of the user", example = "alice@example.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 13)
    @Schema(description = "Phone number of the user", example = "1234567890")
    private String phoneNumber;

    @Schema(description = "Status of the user", example = "ACTIVE")
    private Status status;

    @Schema(description = "Role of the user", example = "ADMIN")
    private Role role;

    @Schema(description = "Whether the user is active", example = "true")
    private Boolean active;

    @Schema(description = "Whether the user must change their password on next login", example = "true")
    private Boolean passwordChangeRequired;
}
