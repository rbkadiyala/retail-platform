package com.example.retailplatform.user.application.service;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
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

class UserServiceExceptionTest {

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

    // ---------------- ResourceNotFoundException ----------------
    @Test
    void getUserById_notFound_throwsException() {
        when(repositoryPort.findActiveById("1")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById("1"));

        assertEquals("User", ex.getResourceName());
        assertEquals("1", ex.getResourceId());
        assertEquals(UserConstants.USER_NOT_FOUND_KEY, ex.getMessageKey());
        verify(repositoryPort).findActiveById("1");
    }

    @Test
    void updateUser_notFound_throwsException() {
        when(repositoryPort.findActiveById("1")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser("1", user));

        assertEquals("User", ex.getResourceName());
        assertEquals("1", ex.getResourceId());
        assertEquals(UserConstants.USER_NOT_FOUND_KEY, ex.getMessageKey());
        verify(repositoryPort).findActiveById("1");
    }

    @Test
    void patchUser_notFound_throwsException() {
        when(repositoryPort.findActiveById("1")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.patchUser("1", user));

        assertEquals("User", ex.getResourceName());
        assertEquals("1", ex.getResourceId());
        assertEquals(UserConstants.USER_NOT_FOUND_KEY, ex.getMessageKey());
        verify(repositoryPort).findActiveById("1");
    }

    @Test
    void softDeleteUser_notFound_noException() {
        doNothing().when(repositoryPort).softDelete("1");
        assertDoesNotThrow(() -> userService.softDeleteUser("1"));
        verify(repositoryPort).softDelete("1");
    }

    // ---------------- ResourceAlreadyExistsException for createUser ----------------
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

    // ---------------- ResourceAlreadyExistsException for updateUser ----------------
    @Test
    void updateUser_usernameAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");

        User duplicate = new User();
        duplicate.setId("2");
        duplicate.setUsername(user.getUsername());

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByUsername(user.getUsername())).thenReturn(Optional.of(duplicate));

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

        User duplicate = new User();
        duplicate.setId("2");
        duplicate.setEmail(user.getEmail());

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByEmail(user.getEmail())).thenReturn(Optional.of(duplicate));

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
        existing.setPhoneNumber("1111111111");

        User duplicate = new User();
        duplicate.setId("2");
        duplicate.setPhoneNumber(user.getPhoneNumber());

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByPhoneNumber(user.getPhoneNumber())).thenReturn(Optional.of(duplicate));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser("1", user));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_PHONE, ex.getFieldName());
        assertEquals(user.getPhoneNumber(), ex.getFieldValue());
        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    // ---------------- ResourceAlreadyExistsException for patchUser ----------------
    @Test
    void patchUser_usernameAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");

        User duplicate = new User();
        duplicate.setId("2");
        duplicate.setUsername(user.getUsername());

        User patch = new User();
        patch.setUsername(user.getUsername());

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByUsername(patch.getUsername())).thenReturn(Optional.of(duplicate));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser("1", patch));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_USERNAME, ex.getFieldName());
        assertEquals(patch.getUsername(), ex.getFieldValue());
        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    @Test
    void patchUser_emailAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");

        User duplicate = new User();
        duplicate.setId("2");
        duplicate.setEmail(user.getEmail());

        User patch = new User();
        patch.setEmail(user.getEmail());

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByEmail(patch.getEmail())).thenReturn(Optional.of(duplicate));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser("1", patch));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_EMAIL, ex.getFieldName());
        assertEquals(patch.getEmail(), ex.getFieldValue());
        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }

    @Test
    void patchUser_phoneNumberAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");

        User duplicate = new User();
        duplicate.setId("2");
        duplicate.setPhoneNumber(user.getPhoneNumber());

        User patch = new User();
        patch.setPhoneNumber(user.getPhoneNumber());

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        when(repositoryPort.findActiveByPhoneNumber(patch.getPhoneNumber())).thenReturn(Optional.of(duplicate));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser("1", patch));

        assertEquals("User", ex.getResourceName());
        assertEquals(UserConstants.FIELD_PHONE, ex.getFieldName());
        assertEquals(patch.getPhoneNumber(), ex.getFieldValue());
        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, ex.getMessageKey());
    }
}
