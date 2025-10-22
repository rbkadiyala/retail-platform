package com.example.retailplatform.auth.jwt.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request with username and password")
public class LoginRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50)
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 100)
    @Schema(description = "Password of the user", example = "secret123")
    private String password;
}
