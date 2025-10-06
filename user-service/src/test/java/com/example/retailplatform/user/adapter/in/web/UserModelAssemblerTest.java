package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.adapter.in.web.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import static org.junit.jupiter.api.Assertions.*;

class UserModelAssemblerTest {

    private UserModelAssembler assembler;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        assembler = new UserModelAssembler();

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
    }

    @Test
    void testToModel() {
        EntityModel<UserResponse> model = assembler.toModel(userResponse);

        assertNotNull(model);
        assertEquals(userResponse, model.getContent());

        // Check self link
        assertTrue(model.getLinks().hasLink("self"));
        assertTrue(model.getLinks().hasLink("users"));

        // Optional: verify URI patterns (just the path contains user ID)
        assertTrue(model.getRequiredLink("self").getHref().contains(userResponse.getId()));
    }
}
