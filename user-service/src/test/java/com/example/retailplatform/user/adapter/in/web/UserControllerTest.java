package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.adapter.in.web.dto.UserDtoMapper;
import com.example.retailplatform.user.adapter.in.web.dto.UserRequest;
import com.example.retailplatform.user.adapter.in.web.dto.UserResponse;
import com.example.retailplatform.user.adapter.in.web.dto.UserSearchRequest;
import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class UserControllerTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private UserModelAssembler assembler;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;
    private EntityModel<UserResponse> entityModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id("1")
                .firstName("Alice")
                .lastName("Smith")
                .username("alice.smith")
                .email("alice@example.com")
                .phoneNumber("1234567890")
                .status(Status.ACTIVE)
                .role(Role.USER)
                .active(true)
                .build();

        userRequest = new UserRequest();
        userRequest.setFirstName("Alice");
        userRequest.setLastName("Smith");
        userRequest.setUsername("alice.smith");
        userRequest.setEmail("alice@example.com");
        userRequest.setPhoneNumber("1234567890");
        userRequest.setStatus(Status.ACTIVE);
        userRequest.setRole(Role.USER);
        userRequest.setActive(true);

        userResponse = UserResponse.builder()
                .id("1")
                .firstName("Alice")
                .lastName("Smith")
                .username("alice.smith")
                .email("alice@example.com")
                .phoneNumber("1234567890")
                .status("ACTIVE")
                .role("USER")
                .active(true)
                .build();

        entityModel = EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel());
    }

    @Test
    void all_returnsCollectionModel() {
        when(userUseCase.getAllUsers()).thenReturn(List.of(user));
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        ResponseEntity<CollectionModel<EntityModel<UserResponse>>> response = userController.all();
        CollectionModel<EntityModel<UserResponse>> result = response.getBody();

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().iterator().next().getLinks().hasLink("self"));
        verify(userUseCase, times(1)).getAllUsers();
    }

    @Test
    void one_returnsEntityModel() {
        when(userUseCase.getUserById("1")).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        ResponseEntity<EntityModel<UserResponse>> response = userController.one("1");
        EntityModel<UserResponse> result = response.getBody();

        assertNotNull(result);
        assertEquals("1", result.getContent().getId());
        assertTrue(result.getLinks().hasLink("self"));
        verify(userUseCase, times(1)).getUserById("1");
    }

    @Test
    void newUser_createsAndReturnsEntityModel() {
        when(userDtoMapper.toModel(userRequest)).thenReturn(user);
        when(userUseCase.createUser(user)).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        ResponseEntity<EntityModel<UserResponse>> response = userController.newUser(userRequest);
        EntityModel<UserResponse> result = response.getBody();

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(result);
        assertTrue(result.getLinks().hasLink("self"));
        verify(userUseCase, times(1)).createUser(user);
    }

    @Test
    void replaceUser_updatesAndReturnsEntityModel() {
        when(userDtoMapper.toModel(userRequest)).thenReturn(user);
        when(userUseCase.updateUser("1", user)).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        ResponseEntity<EntityModel<UserResponse>> response = userController.replaceUser("1", userRequest);
        EntityModel<UserResponse> result = response.getBody();

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(result);
        assertTrue(result.getLinks().hasLink("self"));
        verify(userUseCase, times(1)).updateUser("1", user);
    }

    @Test
    void patchUser_updatesAndReturnsEntityModel() {
        when(userDtoMapper.toModel(userRequest)).thenReturn(user);
        when(userUseCase.patchUser("1", user)).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        ResponseEntity<EntityModel<UserResponse>> response = userController.patchUser("1", userRequest);
        EntityModel<UserResponse> result = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(result);
        assertTrue(result.getLinks().hasLink("self"));
        verify(userUseCase, times(1)).patchUser("1", user);
    }

    @Test
    void deleteUser_callsSoftDelete() {
        doNothing().when(userUseCase).softDeleteUser("1");

        ResponseEntity<Void> response = userController.deleteUser("1");

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(userUseCase, times(1)).softDeleteUser("1");
    }

    @Test
    void search_returnsCollectionModelWithHeaders() {
        UserSearchRequest searchRequest = new UserSearchRequest();
        searchRequest.setEmail("alice@example.com");

        when(userUseCase.searchUsers(null, "alice@example.com", null)).thenReturn(List.of(user));
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        ResponseEntity<CollectionModel<EntityModel<UserResponse>>> response = userController.search(searchRequest);
        CollectionModel<EntityModel<UserResponse>> result = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().iterator().next().getLinks().hasLink("self"));
        assertEquals("1", response.getHeaders().getFirst("X-Total-Count"));

        verify(userUseCase, times(1)).searchUsers(null, "alice@example.com", null);
    }
}
