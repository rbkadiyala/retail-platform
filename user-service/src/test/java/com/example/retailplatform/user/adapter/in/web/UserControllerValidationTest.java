package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.adapter.in.web.dto.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.example.retailplatform.user.adapter.in.web.dto.UserDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock dependencies of UserController
    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private UserDtoMapper userDtoMapper;

    @MockBean
    private UserModelAssembler assembler;

    @Test
    void testCreateUser_InvalidEmail() throws Exception {
        UserRequest request = new UserRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setUsername("alice123");
        request.setEmail("invalid-email"); // invalid email
        request.setPhoneNumber("1234567890");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field=='email')]").exists());
    }

    @Test
    void testCreateUser_MissingRequiredFields() throws Exception {
        UserRequest request = new UserRequest(); // all fields null

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field=='firstName')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='lastName')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='username')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='email')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='phoneNumber')]").exists());
    }

    @Test
    void testCreateUser_PhoneNumberTooShort() throws Exception {
        UserRequest request = new UserRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setUsername("alice123");
        request.setEmail("alice@example.com");
        request.setPhoneNumber("12345"); // too short

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field=='phoneNumber')]").exists());
    }
}
