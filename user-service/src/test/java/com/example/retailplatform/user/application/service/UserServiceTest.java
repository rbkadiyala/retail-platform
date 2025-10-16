package com.example.retailplatform.user.application.service;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.model.Role;
import com.example.retailplatform.user.domain.model.Status;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepositoryPort repositoryPort;

    @InjectMocks
    private UserService userService;

    private User user;

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
    }

    // -------------------- createUser uniqueness --------------------
    @Test
    void createUser_usernameAlreadyExists_throwsException() {
        when(repositoryPort.existsByUsername(user.getUsername())).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_USERNAME, ex.getFieldName());
        assertEquals(user.getUsername(), ex.getFieldValue());
        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        when(repositoryPort.existsByActiveEmail(user.getEmail())).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_EMAIL, ex.getFieldName());
        assertEquals(user.getEmail(), ex.getFieldValue());
        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    @Test
    void createUser_phoneNumberAlreadyExists_throwsException() {
        when(repositoryPort.existsByActivePhoneNumber(user.getPhoneNumber())).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_PHONE, ex.getFieldName());
        assertEquals(user.getPhoneNumber(), ex.getFieldValue());
        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    // -------------------- updateUser uniqueness --------------------
    @Test
    void updateUser_usernameAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");
        existing.setUsername("oldUsername");

        User conflictingUser = new User();
        conflictingUser.setId("2"); // different ID
        conflictingUser.setUsername("alice.smith"); // same username to trigger uniqueness

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.existsByUsername("alice.smith")).thenReturn(true);
        when(repositoryPort.findActiveByUsername("alice.smith")).thenReturn(Optional.of(conflictingUser));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser("1", user));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_USERNAME, ex.getFieldName());
        assertEquals(user.getUsername(), ex.getFieldValue());
        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    @Test
    void updateUser_emailAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");
        existing.setEmail("old@example.com");

        User conflictingUser = new User();
        conflictingUser.setId("2"); // different ID
        conflictingUser.setEmail(user.getEmail()); // same email to trigger uniqueness

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByEmail(user.getEmail())).thenReturn(Optional.of(conflictingUser));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser("1", user));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_EMAIL, ex.getFieldName());
        assertEquals(user.getEmail(), ex.getFieldValue());
        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    @Test
    void updateUser_phoneNumberAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");
        existing.setPhoneNumber("0987654321");

        User conflictingUser = new User();
        conflictingUser.setId("2"); // different ID
        conflictingUser.setPhoneNumber(user.getPhoneNumber()); // same phone to trigger uniqueness

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(conflictingUser));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser("1", user));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_PHONE, ex.getFieldName());
        assertEquals(user.getPhoneNumber(), ex.getFieldValue());
        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }
}
