package com.example.retailplatform.user.domain.exception;

import com.example.retailplatform.user.domain.UserConstants;
import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    private final String field;
    private final String key;

    // Only key is passed, message will be resolved in the handler
    public ResourceAlreadyExistsException(String field, String key) {
        super(); // No message here
        this.field = field;
        this.key = key;
    }

    // Default key constructor
    public ResourceAlreadyExistsException(String field) {
        super();
        this.field = field;
        this.key = UserConstants.USER_ALREADY_EXISTS_KEY;
    }
}
