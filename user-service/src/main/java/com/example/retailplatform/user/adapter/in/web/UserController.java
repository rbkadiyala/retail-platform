package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.adapter.in.web.dto.UserRequest;
import com.example.retailplatform.user.adapter.in.web.dto.UserResponse;
import com.example.retailplatform.user.adapter.in.web.dto.UserDtoMapper;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.example.retailplatform.user.common.ErrorResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final UserDtoMapper userDtoMapper;
    private final UserModelAssembler assembler;

    // ------------------ GET ALL USERS ------------------
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "401", description = "Authentication failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> all() {
        List<EntityModel<UserResponse>> users = userUseCase.getAllUsers().stream()
                .map(userDtoMapper::toResponse)
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    // ------------------ GET USER BY ID ------------------
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                            @ExampleObject(
                                name = "UserNotFound",
                                value = """
                                        {
                                          "timestamp": "2025-10-16T14:50:00",
                                          "status": 404,
                                          "error": "RESOURCE_NOT_FOUND",
                                          "messageKey": "user.not.found",
                                          "message": "User with ID 123 not found",
                                          "path": "/api/users/123",
                                          "resource": "User",
                                          "errors": [
                                            {"fieldName": "id", "fieldValue": "123", "message": "Resource not found"}
                                          ]
                                        }
                                        """
                            )
                        }
                )),
        @ApiResponse(responseCode = "401", description = "Authentication failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> one(@PathVariable String id) {
        User user = userUseCase.getUserById(id);
        return assembler.toModel(userDtoMapper.toResponse(user));
    }

    // ------------------ CREATE NEW USER ------------------
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                            @ExampleObject(
                                name = "ValidationFailed",
                                value = """
                                        {
                                          "timestamp": "2025-10-16T14:55:30",
                                          "status": 400,
                                          "error": "VALIDATION_FAILED",
                                          "messageKey": "validation.failed",
                                          "message": "Validation failed",
                                          "path": "/api/users",
                                          "resource": null,
                                          "errors": [
                                            {"fieldName": "email", "fieldValue": "invalid-email", "message": "Email must be valid"},
                                            {"fieldName": "password", "fieldValue": null, "message": "Password is required"}
                                          ]
                                        }
                                        """
                            )
                        }
                )),
        @ApiResponse(responseCode = "409", description = "User already exists",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                            @ExampleObject(
                                name = "UserAlreadyExists",
                                value = """
                                        {
                                          "timestamp": "2025-10-16T14:52:10",
                                          "status": 409,
                                          "error": "RESOURCE_ALREADY_EXISTS",
                                          "messageKey": "user.already.exists",
                                          "message": "User with email test@example.com already exists",
                                          "path": "/api/users",
                                          "resource": "User",
                                          "errors": [
                                            {"fieldName": "email", "fieldValue": "test@example.com", "message": "Duplicate field value"}
                                          ]
                                        }
                                        """
                            )
                        }
                )),
        @ApiResponse(responseCode = "401", description = "Authentication failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> newUser(@Valid @RequestBody UserRequest request) {
        User created = userUseCase.createUser(userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(created));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ------------------ UPDATE USER (PUT) ------------------
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                            @ExampleObject(
                                name = "UserNotFound",
                                value = """
                                        {
                                          "timestamp": "2025-10-16T15:00:00",
                                          "status": 404,
                                          "error": "RESOURCE_NOT_FOUND",
                                          "messageKey": "user.not.found",
                                          "message": "User with ID 123 not found",
                                          "path": "/api/users/123",
                                          "resource": "User",
                                          "errors": [
                                            {"fieldName": "id", "fieldValue": "123", "message": "Resource not found"}
                                          ]
                                        }
                                        """
                            )
                        }
                )),
        @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> replaceUser(@PathVariable String id,
                                                                 @Valid @RequestBody UserRequest request) {
        User updated = userUseCase.updateUser(id, userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(updated));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // ------------------ PATCH USER ------------------
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User patched successfully"),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> patchUser(@PathVariable String id,
                                                               @Valid @RequestBody UserRequest request) {
        User patched = userUseCase.patchUser(id, userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(patched));

        return ResponseEntity.ok(entityModel);
    }

    // ------------------ DELETE USER ------------------
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Authentication failed",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userUseCase.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
