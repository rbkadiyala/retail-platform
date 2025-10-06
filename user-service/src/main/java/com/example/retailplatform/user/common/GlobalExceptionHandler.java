package com.example.retailplatform.user.common;

import com.example.retailplatform.user.domain.UserConstants;
import com.example.retailplatform.user.domain.exception.ResourceAlreadyExistsException;
import com.example.retailplatform.user.domain.exception.ResourceNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger appLogger = LogManager.getLogger("com.example.retailplatform");

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String key = ex.getKey() != null ? ex.getKey() : UserConstants.USER_NOT_FOUND_KEY;
        String message = getMessage(key);
        return buildErrorResponse(HttpStatus.NOT_FOUND, message, key, request.getRequestURI(), null, null, ex);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        String key = ex.getKey() != null ? ex.getKey() : UserConstants.USER_ALREADY_EXISTS_KEY;
        String message = getMessage(key);
        return buildErrorResponse(HttpStatus.CONFLICT, message, key, request.getRequestURI(), ex.getField(), null, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        String key = UserConstants.VALIDATION_FAILED_KEY;
        String message = getMessage(key, new Object[]{validationErrors.size()});

        // Return 400 instead of 422
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                key,
                request.getRequestURI(),
                null,
                validationErrors,
                ex
        );
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String key = UserConstants.CONSTRAINT_VIOLATION_KEY;
        String message = getMessage(key);
        return buildErrorResponse(HttpStatus.CONFLICT, message, key, request.getRequestURI(), null, null, ex);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handlePersistenceException(PersistenceException ex, HttpServletRequest request) {
        String key = UserConstants.PERSISTENCE_EXCEPTION_KEY;
        String message = getMessage(key);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, key, request.getRequestURI(), null, null, ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String key = UserConstants.RUNTIME_EXCEPTION_KEY;
        String message = getMessage(key);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, key, request.getRequestURI(), null, null, ex);
    }    

    // ---------------- Generic error builder ----------------
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String key, String path,
                                                             String field, List<ValidationError> validationErrors, Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .key(key)
                .field(field)
                .path(path)
                .validationErrors(validationErrors)
                .build();

        if (status.is4xxClientError()) {
            appLogger.warn("{}", error, ex);
        } else {
            appLogger.error("{}", error, ex);
        }

        return ResponseEntity.status(status).body(error);
    }

    private ValidationError mapFieldError(FieldError fieldError) {
        return ValidationError.builder()
                .field(fieldError.getField())
                .value(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    // ---------------- i18n message helper ----------------
    private String getMessage(String key, Object... args) {
        String msg = messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
        System.out.println("Resolved message for key '" + key + "' with locale " + LocaleContextHolder.getLocale() + " : " + msg);
        return msg;
    }
}
