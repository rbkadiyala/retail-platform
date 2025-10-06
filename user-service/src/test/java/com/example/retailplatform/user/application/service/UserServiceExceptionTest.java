package com.example.retailplatform.user.application.service;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import com.example.retailplatform.user.domain.model.User;
import com.example.retailplatform.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceExceptionTest {

    @Mock
    private UserRepositoryPort repositoryPort;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------- ResourceNotFoundException ----------------

    @Test
    void getUserById_notFound_throwsException() {
        String userId = "1";
        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals(UserConstants.USER_NOT_FOUND_KEY, exception.getKey());
        verify(repositoryPort, times(1)).findActiveById(userId);
    }

    @Test
    void updateUser_notFound_throwsException() {
        String userId = "1";
        User user = new User();
        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(userId, user));

        assertEquals(UserConstants.USER_NOT_FOUND_KEY, exception.getKey());
        verify(repositoryPort, times(1)).findActiveById(userId);
    }

    @Test
    void patchUser_notFound_throwsException() {
        String userId = "1";
        User user = new User();
        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.patchUser(userId, user));

        assertEquals(UserConstants.USER_NOT_FOUND_KEY, exception.getKey());
        verify(repositoryPort, times(1)).findActiveById(userId);
    }

    @Test
    void softDeleteUser_notFound_throwsException() {
        String userId = "1";
        doThrow(new ResourceNotFoundException(UserConstants.USER_NOT_FOUND_KEY))
                .when(repositoryPort).softDelete(userId);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.softDeleteUser(userId));

        assertEquals(UserConstants.USER_NOT_FOUND_KEY, exception.getKey());
        verify(repositoryPort, times(1)).softDelete(userId);
    }

    // ---------------- ResourceAlreadyExistsException for createUser ----------------

    @Test
    void createUser_usernameAlreadyExists_throwsException() {
        User user = new User();
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY))
                .when(repositoryPort).save(user);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_USERNAME, exception.getField());
        verify(repositoryPort, times(1)).save(user);
    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        User user = new User();
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY))
                .when(repositoryPort).save(user);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_EMAIL, exception.getField());
        verify(repositoryPort, times(1)).save(user);
    }

    @Test
    void createUser_phoneNumberAlreadyExists_throwsException() {
        User user = new User();
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY))
                .when(repositoryPort).save(user);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(user));

        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_PHONE, exception.getField());
        verify(repositoryPort, times(1)).save(user);
    }

    // ---------------- ResourceAlreadyExistsException for updateUser ----------------

    @Test
    void updateUser_usernameAlreadyExists_throwsException() {
        String userId = "1";
        User existing = new User();
        existing.setId(userId);
        User user = new User();

        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser(userId, user));

        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_USERNAME, exception.getField());
        verify(repositoryPort, times(1)).findActiveById(userId);
        verify(repositoryPort, times(1)).patch(existing);
    }

    @Test
    void updateUser_emailAlreadyExists_throwsException() {
        String userId = "1";
        User existing = new User();
        existing.setId(userId);
        User user = new User();

        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser(userId, user));

        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_EMAIL, exception.getField());
        verify(repositoryPort, times(1)).findActiveById(userId);
        verify(repositoryPort, times(1)).patch(existing);
    }

    @Test
    void updateUser_phoneNumberAlreadyExists_throwsException() {
        String userId = "1";
        User existing = new User();
        existing.setId(userId);
        User user = new User();

        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser(userId, user));

        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_PHONE, exception.getField());
        verify(repositoryPort, times(1)).findActiveById(userId);
        verify(repositoryPort, times(1)).patch(existing);
    }

    // ---------------- ResourceAlreadyExistsException for patchUser ----------------

    @Test
    void patchUser_usernameAlreadyExists_throwsException() {
        String userId = "1";
        User existing = new User();
        existing.setId(userId);
        User patch = new User();
        patch.setUsername("existingUsername");

        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser(userId, patch));

        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_USERNAME, exception.getField());
        verify(repositoryPort, times(1)).findActiveById(userId);
        verify(repositoryPort, times(1)).patch(existing);
    }

    @Test
    void patchUser_emailAlreadyExists_throwsException() {
        String userId = "1";
        User existing = new User();
        existing.setId(userId);
        User patch = new User();
        patch.setEmail("existing@example.com");

        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_EMAIL, UserConstants.EMAIL_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser(userId, patch));

        assertEquals(UserConstants.EMAIL_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_EMAIL, exception.getField());
        verify(repositoryPort, times(1)).findActiveById(userId);
        verify(repositoryPort, times(1)).patch(existing);
    }

    @Test
    void patchUser_phoneNumberAlreadyExists_throwsException() {
        String userId = "1";
        User existing = new User();
        existing.setId(userId);
        User patch = new User();
        patch.setPhoneNumber("1234567890");

        when(repositoryPort.findActiveById(userId)).thenReturn(Optional.of(existing));
        doThrow(new ResourceAlreadyExistsException(UserConstants.FIELD_PHONE, UserConstants.PHONE_ALREADY_EXISTS_KEY))
                .when(repositoryPort).patch(existing);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser(userId, patch));

        assertEquals(UserConstants.PHONE_ALREADY_EXISTS_KEY, exception.getKey());
        assertEquals(UserConstants.FIELD_PHONE, exception.getField());
        verify(repositoryPort, times(1)).findActiveById(userId);
        verify(repositoryPort, times(1)).patch(existing);
    }
}
