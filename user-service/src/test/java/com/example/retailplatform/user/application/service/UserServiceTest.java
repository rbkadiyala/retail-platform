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

class UserServiceUniquenessTest {

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
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY))
                .when(repositoryPort).save(user);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_USERNAME, ex.getField());
        verify(repositoryPort, times(1)).save(user);
    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY))
                .when(repositoryPort).save(user);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_EMAIL, ex.getField());
        verify(repositoryPort, times(1)).save(user);
    }

    @Test
    void createUser_phoneNumberAlreadyExists_throwsException() {
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY))
                .when(repositoryPort).save(user);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_PHONE, ex.getField());
        verify(repositoryPort, times(1)).save(user);
    }

    // -------------------- updateUser uniqueness --------------------

    @Test
    void updateUser_usernameAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser("1", user));

        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_USERNAME, ex.getField());
        verify(repositoryPort).findActiveById("1");
        verify(repositoryPort).patch(existing);
    }

    @Test
    void updateUser_emailAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser("1", user));

        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_EMAIL, ex.getField());
        verify(repositoryPort).findActiveById("1");
        verify(repositoryPort).patch(existing);
    }

    @Test
    void updateUser_phoneNumberAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser("1", user));

        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_PHONE, ex.getField());
        verify(repositoryPort).findActiveById("1");
        verify(repositoryPort).patch(existing);
    }

    // -------------------- patchUser uniqueness --------------------

    @Test
    void patchUser_usernameAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");
        User patch = new User();
        patch.setUsername("existingUsername");

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser("1", patch));

        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_USERNAME, ex.getField());
        verify(repositoryPort).findActiveById("1");
        verify(repositoryPort).patch(existing);
    }

    @Test
    void patchUser_emailAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");
        User patch = new User();
        patch.setEmail("existing@example.com");

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser("1", patch));

        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_EMAIL, ex.getField());
        verify(repositoryPort).findActiveById("1");
        verify(repositoryPort).patch(existing);
    }

    @Test
    void patchUser_phoneNumberAlreadyExists_throwsException() {
        User existing = new User();
        existing.setId("1");
        User patch = new User();
        patch.setPhoneNumber("1234567890");

        when(repositoryPort.findActiveById("1")).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser("1", patch));

        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, ex.getKey());
        assertEquals(UserConstants.FIELD_PHONE, ex.getField());
        verify(repositoryPort).findActiveById("1");
        verify(repositoryPort).patch(existing);
    }
}
