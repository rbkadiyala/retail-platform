package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.example.retailplatform.user.adapter.in.web.dto.UserDtoMapper;
import com.example.retailplatform.user.adapter.in.web.dto.UserRequest;
import com.example.retailplatform.user.adapter.in.web.dto.UserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LogManager.getLogger(UserController.class);

    private final UserUseCase userUseCase;
    private final UserDtoMapper userDtoMapper;
    private final UserModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> all() {
        List<EntityModel<UserResponse>> users = userUseCase.getAllUsers().stream()
                .map(userDtoMapper::toResponse)
                .map(assembler::toModel)
                .toList(); // ✅ Cleaner and unmodifiable list

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> newUser(@Valid @RequestBody UserRequest request) {
        User created = userUseCase.createUser(userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(created));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/{id}")
    public EntityModel<UserResponse> one(@PathVariable String id) {
        User user = userUseCase.getUserById(id);
        return assembler.toModel(userDtoMapper.toResponse(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> replaceUser(@PathVariable String id,
                                                                 @Valid @RequestBody UserRequest request) {
        User updated = userUseCase.updateUser(id, userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(updated));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> patchUser(@PathVariable String id,
                                                               @Valid @RequestBody UserRequest request) {
        User patched = userUseCase.patchUser(id, userDtoMapper.toModel(request));
        EntityModel<UserResponse> entityModel = assembler.toModel(userDtoMapper.toResponse(patched));

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userUseCase.softDeleteUser(id);
        return ResponseEntity.noContent().build(); // 204
    }

}
