package com.example.retailplatform.user.domain.exception;

import com.example.retailplatform.user.domain.UserConstants;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String key;

    // Only key is passed, message will be resolved in the handler
    public ResourceNotFoundException(String key) {
        super(); // No message here
        this.key = key;
    }

    // Default key constructor
    public ResourceNotFoundException() {
        super();
        this.key = UserConstants.USER_NOT_FOUND_KEY;
    }
}
