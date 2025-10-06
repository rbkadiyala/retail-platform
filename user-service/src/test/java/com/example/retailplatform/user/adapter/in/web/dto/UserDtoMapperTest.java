package com.example.retailplatform.user.adapter.in.web.dto;

import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoMapperTest {

    private UserDtoMapper mapper;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        mapper = new UserDtoMapper();

        userRequest = new UserRequest();
        userRequest.setFirstName("Alice");
        userRequest.setLastName("Smith");
        userRequest.setUsername("alice.smith");
        userRequest.setEmail("alice@example.com");
        userRequest.setPhoneNumber("1234567890");
        userRequest.setStatus(Status.ACTIVE);
        userRequest.setRole(Role.USER);
        userRequest.setActive(true);

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
    }

    @Test
    void testToModel() {
        User mapped = mapper.toModel(userRequest);

        assertNotNull(mapped);
        assertEquals("Alice", mapped.getFirstName());
        assertEquals("Smith", mapped.getLastName());
        assertEquals("alice.smith", mapped.getUsername());
        assertEquals("alice@example.com", mapped.getEmail());
        assertEquals("1234567890", mapped.getPhoneNumber());
        assertEquals(Status.ACTIVE, mapped.getStatus());
        assertEquals(Role.USER, mapped.getRole());
        assertTrue(mapped.getActive());
    }

    @Test
    void testToModel_NullRequest() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void testToResponse() {
        var response = mapper.toResponse(user);

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("Alice", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("alice.smith", response.getUsername());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("1234567890", response.getPhoneNumber());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("USER", response.getRole());
        assertTrue(response.isActive());
    }

    @Test
    void testToResponse_NullUser() {
        assertNull(mapper.toResponse(null));
    }
}
