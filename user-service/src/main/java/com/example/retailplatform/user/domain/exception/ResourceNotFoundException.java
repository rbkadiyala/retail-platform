package com.example.retailplatform.user.domain.exception;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final transient Serializable resourceId; // now safe for serialization
    private final String messageKey;

    /**
     * Constructs an exception using a message key defined in Constants.
     *
     * @param resourceName Name of the resource (e.g., "User")
     * @param resourceId   ID of the missing resource, must be Serializable
     * @param messageKey   Key from messages.properties for i18n
     */
    public ResourceNotFoundException(String resourceName, Serializable resourceId, String messageKey) {
        super(); // message will be resolved in GlobalExceptionHandler
        this.resourceName = resourceName;
        this.resourceId = resourceId;
        this.messageKey = messageKey;
    }
}
