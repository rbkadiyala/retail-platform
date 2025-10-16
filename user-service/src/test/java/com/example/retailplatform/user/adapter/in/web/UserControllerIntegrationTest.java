package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.adapter.in.web.dto.UserDtoMapper;
import com.example.retailplatform.user.adapter.in.web.dto.UserRequest;
import com.example.retailplatform.user.adapter.in.web.dto.UserResponse;
import com.example.retailplatform.user.common.GlobalExceptionHandler;
import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for {@link UserController} using Spring Boot 3.4+ style mock configuration.
 * Replaces deprecated @MockBean with @TestConfiguration and Mockito beans.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, UserControllerIntegrationTest.MockBeans.class})
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserUseCase userUseCase;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private UserModelAssembler assembler;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;
    private EntityModel<UserResponse> entityModel;

    /**
     * Provides mock beans to the Spring test context.
     * This is the new recommended approach instead of @MockBean.
     */
    @org.springframework.boot.test.context.TestConfiguration
    static class MockBeans {

        @Bean
        UserUseCase userUseCase() {
            return Mockito.mock(UserUseCase.class);
        }

        @Bean
        UserDtoMapper userDtoMapper() {
            return Mockito.mock(UserDtoMapper.class);
        }

        @Bean
        UserModelAssembler userModelAssembler() {
            return Mockito.mock(UserModelAssembler.class);
        }
    }

    @BeforeEach
    void setUp() {
        // --- valid User domain model ---
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

        // --- valid UserRequest ---
        userRequest = new UserRequest();
        userRequest.setFirstName("Alice");
        userRequest.setLastName("Smith");
        userRequest.setUsername("alice.smith");
        userRequest.setEmail("alice@example.com");
        userRequest.setPhoneNumber("1234567890");
        userRequest.setStatus(Status.ACTIVE);
        userRequest.setRole(Role.USER);
        userRequest.setActive(true);

        // --- valid UserResponse ---
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

        // --- EntityModel with self link ---
        entityModel = EntityModel.of(userResponse, Link.of("/api/users/1").withSelfRel());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userUseCase.getAllUsers()).thenReturn(List.of(user));
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userList[0].id").value("1"))
                .andExpect(jsonPath("$._embedded.userList[0].firstName").value("Alice"));

        verify(userUseCase, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() throws Exception {
        when(userUseCase.getUserById("1")).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("Alice"));

        verify(userUseCase, times(1)).getUserById("1");
    }

    @Test
    void testCreateUser() throws Exception {
        when(userDtoMapper.toModel(userRequest)).thenReturn(user);
        when(userUseCase.createUser(user)).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("Alice"));

        verify(userUseCase, times(1)).createUser(user);
    }

    @Test
    void testReplaceUser() throws Exception {
        when(userDtoMapper.toModel(userRequest)).thenReturn(user);
        when(userUseCase.updateUser("1", user)).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));

        verify(userUseCase, times(1)).updateUser("1", user);
    }

    @Test
    void testPatchUser() throws Exception {
        when(userDtoMapper.toModel(userRequest)).thenReturn(user);
        when(userUseCase.patchUser("1", user)).thenReturn(user);
        when(userDtoMapper.toResponse(user)).thenReturn(userResponse);
        when(assembler.toModel(userResponse)).thenReturn(entityModel);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(userUseCase, times(1)).patchUser("1", user);
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userUseCase).softDeleteUser("1");

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userUseCase, times(1)).softDeleteUser("1");
    }
}
