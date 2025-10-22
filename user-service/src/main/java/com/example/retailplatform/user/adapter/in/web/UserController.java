package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.adapter.in.web.dto.UserRequest;
import com.example.retailplatform.user.adapter.in.web.dto.UserResponse;
import com.example.retailplatform.user.adapter.in.web.dto.UserSearchRequest;
import com.example.retailplatform.user.adapter.in.web.dto.AuthRequest;
import com.example.retailplatform.user.adapter.in.web.dto.AuthResponse;
import com.example.retailplatform.user.adapter.in.web.dto.UserDtoMapper;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.example.retailplatform.user.common.ErrorResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final UserDtoMapper userDtoMapper;
    private final UserModelAssembler assembler;

    // ------------------ GET ALL USERS ------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve all users in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponse>>> all() {
        List<User> users = userUseCase.getAllUsers();
        List<EntityModel<UserResponse>> userModels = users.stream()
                .map(userDtoMapper::toResponse)
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserResponse>> body = CollectionModel.of(
                userModels,
                linkTo(methodOn(UserController.class).all()).withSelfRel()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(users.size()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }

    // ------------------ GET USER BY ID ------------------
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<EntityModel<UserResponse>> one(@PathVariable String id) {
        User user = userUseCase.getUserById(id);
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(user));
        return ResponseEntity.ok(entityModel);
    }

    // ------------------ CREATE NEW USER ------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Create a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> newUser(@Valid @RequestBody UserRequest request) {
        User created = userUseCase.createUser(userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(created));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ------------------ UPDATE USER (PUT) ------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Replace an existing user entirely by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<EntityModel<UserResponse>> replaceUser(@PathVariable String id,
                                                                 @Valid @RequestBody UserRequest request) {
        User updated = userUseCase.updateUser(id, userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(updated));

        return ResponseEntity.ok(entityModel);
    }

    // ------------------ PATCH USER ------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Patch user", description = "Partially update an existing user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User patched successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id:\\d+}")
    public ResponseEntity<EntityModel<UserResponse>> patchUser(@PathVariable String id,
                                                               @Valid @RequestBody UserRequest request) {
        User patched = userUseCase.patchUser(id, userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(patched));

        return ResponseEntity.ok(entityModel);
    }

    // ------------------ DELETE USER ------------------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Soft delete a user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userUseCase.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ SEARCH USERS ------------------
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Search users",
            description = "Search users by username, email, or phone number. All parameters are optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<UserResponse>>> search(
            @RequestBody UserSearchRequest searchRequest) {

        List<User> users = userUseCase.searchUsers(
                searchRequest.getUsername(),
                searchRequest.getEmail(),
                searchRequest.getPhoneNumber()
        );

        List<EntityModel<UserResponse>> userModels = users.stream()
                .map(userDtoMapper::toResponse)
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserResponse>> body = CollectionModel.of(
                userModels,
                linkTo(methodOn(UserController.class).search(searchRequest)).withSelfRel()
        );

        HttpHeaders headers = new HttpHeaders();

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }

    // ------------------ AUTHENTICATE ------------------
    @Operation(summary = "Authenticate user", description = "Login endpoint to obtain access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userUseCase.authenticate(request));
    }
}
