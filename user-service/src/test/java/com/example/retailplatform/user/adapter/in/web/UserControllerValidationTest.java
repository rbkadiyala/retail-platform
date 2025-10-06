package com.example.retailplatform.user.adapter.in.web;

import com.example.retailplatform.user.adapter.in.web.dto.UserRequest;
import com.example.retailplatform.user.adapter.in.web.dto.UserDtoMapper;
import com.example.retailplatform.user.domain.port.in.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Validation tests for {@link UserController}.
 * Uses Spring Boot 3.4+ compatible mock configuration.
 */
@WebMvcTest(UserController.class)
@Import(UserControllerValidationTest.MockBeans.class)
public class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    //@Autowired
    //private UserUseCase userUseCase;

    //@Autowired
    //private UserDtoMapper userDtoMapper;

    //@Autowired
    //private UserModelAssembler assembler;

    /**
     * Provides mock beans instead of using deprecated @MockBean.
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
