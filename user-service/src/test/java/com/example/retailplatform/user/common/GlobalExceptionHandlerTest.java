package com.example.retailplatform.user.common;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        var ex = new ResourceNotFoundException(UserConstants.USER_NOT_FOUND_KEY);

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(UserConstants.USER_NOT_FOUND_KEY, response.getBody().getKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
    }

    // ---------------- ResourceAlreadyExistsException ----------------
    @Test
    void handleResourceAlreadyExists() {
        var ex = new ResourceAlreadyExistsException(UserConstants.FIELD_USERNAME, UserConstants.USERNAME_ALREADY_EXISTS_KEY);

        ResponseEntity<ErrorResponse> response = handler.handleResourceAlreadyExists(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(UserConstants.USERNAME_ALREADY_EXISTS_KEY, response.getBody().getKey());
        assertEquals(UserConstants.FIELD_USERNAME, response.getBody().getField());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
    }

    // ---------------- Validation Exception ----------------
    @Test
    void handleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("user", UserConstants.FIELD_USERNAME, "must not be empty");

        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(UserConstants.VALIDATION_FAILED_KEY, response.getBody().getKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());

        List<ValidationError> errors = response.getBody().getValidationErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(UserConstants.FIELD_USERNAME, errors.get(0).getField());
    }

    // ---------------- ConstraintViolationException ----------------
    @Test
    void handleConstraintViolation() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(UserConstants.CONSTRAINT_VIOLATION_KEY, response.getBody().getKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
    }

    // ---------------- PersistenceException ----------------
    @Test
    void handlePersistenceException() {
        PersistenceException ex = new PersistenceException("DB error");

        ResponseEntity<ErrorResponse> response = handler.handlePersistenceException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(UserConstants.PERSISTENCE_EXCEPTION_KEY, response.getBody().getKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
    }

    // ---------------- RuntimeException ----------------
    @Test
    void handleRuntimeException() {
        RuntimeException ex = new RuntimeException("Runtime error");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(UserConstants.RUNTIME_EXCEPTION_KEY, response.getBody().getKey());
        assertEquals(UserConstants.REQUEST_URI, response.getBody().getPath());
    }
}
