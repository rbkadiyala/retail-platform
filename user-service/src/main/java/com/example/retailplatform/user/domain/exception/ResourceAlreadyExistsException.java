package com.example.retailplatform.user.domain.exception;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final String fieldName;
    private final transient Serializable fieldValue; // safe for serialization
    private final String messageKey;

    /**
     * Constructs an exception using a message key defined in Constants.
     *
     * @param resourceName Name of the resource (e.g., "User")
     * @param fieldName    Name of the field that caused the conflict
     * @param fieldValue   Value of the conflicting field, must be Serializable
     * @param messageKey   Key from messages.properties for i18n
     */
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Serializable fieldValue, String messageKey) {
        super(); // message will be resolved in GlobalExceptionHandler
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.messageKey = messageKey;
    }
}
