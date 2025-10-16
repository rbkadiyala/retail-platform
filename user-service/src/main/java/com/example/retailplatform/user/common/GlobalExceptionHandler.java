package com.example.retailplatform.user.common;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // ------------------ Domain Exceptions ------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String message = getMessage(ex.getMessageKey(), ex.getResourceName(), ex.getResourceId());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                ex.getMessageKey(),
                request.getRequestURI(),
                message,
                ex.getResourceName(),
                List.of(ErrorResponse.Error.builder()
                        .fieldName("id")
                        .fieldValue(ex.getResourceId())
                        .message("Resource not found")
                        .build())
        );

        log.warn("Resource not found: {}", error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        String message = getMessage(ex.getMessageKey(), ex.getResourceName(), ex.getFieldName(), ex.getFieldValue());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "RESOURCE_ALREADY_EXISTS",
                ex.getMessageKey(),
                request.getRequestURI(),
                message,
                ex.getResourceName(),
                List.of(ErrorResponse.Error.builder()
                        .fieldName(ex.getFieldName())
                        .fieldValue(ex.getFieldValue())
                        .message("Duplicate field value")
                        .build())
        );

        log.warn("Resource already exists: {}", error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ------------------ Validation ------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.Error> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                UserConstants.VALIDATION_FAILED_KEY,
                request.getRequestURI(),
                "Validation failed",
                null,
                validationErrors
        );

        log.warn("Validation failed: {}", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ErrorResponse.Error mapFieldError(FieldError fieldError) {
        return ErrorResponse.Error.builder()
                .fieldName(fieldError.getField())
                .fieldValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    // ------------------ Security ------------------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        String message = getMessage(UserConstants.ERROR_AUTH_FAILED);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "AUTHENTICATION_FAILED",
                UserConstants.ERROR_AUTH_FAILED,
                request.getRequestURI(),
                message,
                null,
                List.of()
        );

        log.warn("Authentication failed: {}", error);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String message = getMessage(UserConstants.ERROR_ACCESS_DENIED);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                UserConstants.ERROR_ACCESS_DENIED,
                request.getRequestURI(),
                message,
                null,
                List.of()
        );

        log.warn("Access denied: {}", error);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ------------------ Generic Exception ------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        String message = getMessage(UserConstants.ERROR_INTERNAL_SERVER, ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                UserConstants.ERROR_INTERNAL_SERVER,
                request.getRequestURI(),
                message,
                null,
                List.of()
        );

        log.error("Server error: {}", error, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ------------------ Helper ------------------

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }
}
