package com.example.retailplatform.user.common;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private MessageSource messageSource;
    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        handler = new GlobalExceptionHandler(messageSource);
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn(UserConstants.REQUEST_URI);
        when(messageSource.getMessage(anyString(), any(), anyString(), any()))
                .thenAnswer(invocation -> invocation.getArgument(2));
    }

    // ---------------- ResourceNotFoundException ----------------
    @Test
    void handleResourceNotFound() {
        var ex = new ResourceNotFoundException("User", "123", UserConstants.USER_NOT_FOUND_KEY);

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(UserConstants.USER_NOT_FOUND_KEY, response.getBody().getMessageKey());
        assertEquals("User", response.getBody().getResource());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());

        List<ErrorResponse.Error> errors = response.getBody().getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("id", errors.get(0).getFieldName());
        assertEquals("123", errors.get(0).getFieldValue());
        assertEquals("Resource not found", errors.get(0).getMessage());
    }

    // ---------------- ResourceAlreadyExistsException ----------------
    @Test
    void handleResourceAlreadyExists() {
        var ex = new ResourceAlreadyExistsException("User", "username", "john.doe", UserConstants.USERNAME_ALREADY_EXISTS_KEY);

        ResponseEntity<ErrorResponse> response = handler.handleResourceAlreadyExists(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, response.getBody().getMessageKey());
        assertEquals("User", response.getBody().getResource());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());

        List<ErrorResponse.Error> errors = response.getBody().getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("username", errors.get(0).getFieldName());
        assertEquals("john.doe", errors.get(0).getFieldValue());
        assertEquals("Duplicate field value", errors.get(0).getMessage());
    }

    // ---------------- Validation Exception ----------------
    @Test
    void handleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("user", "username", "must not be empty");

        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(UserConstants.VALIDATION_FAILED_KEY, response.getBody().getMessageKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
        assertNull(response.getBody().getResource());

        List<ErrorResponse.Error> errors = response.getBody().getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("username", errors.get(0).getFieldName());
        assertEquals("must not be empty", errors.get(0).getMessage());
        assertNull(errors.get(0).getFieldValue());
    }

    // ---------------- AuthenticationException (401) ----------------
    @Test
    void handleAuthenticationException() {
        AuthenticationException ex = mock(AuthenticationException.class);

        ResponseEntity<ErrorResponse> response = handler.handleAuthentication(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(UserConstants.ERROR_AUTH_FAILED, response.getBody().getMessageKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
        assertNull(response.getBody().getResource());
        assertTrue(response.getBody().getErrors().isEmpty());
    }

    // ---------------- AccessDeniedException (403) ----------------
    @Test
    void handleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(UserConstants.ERROR_ACCESS_DENIED, response.getBody().getMessageKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
        assertNull(response.getBody().getResource());
        assertTrue(response.getBody().getErrors().isEmpty());
    }
}
