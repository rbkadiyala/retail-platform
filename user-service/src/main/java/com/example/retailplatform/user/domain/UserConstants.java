package com.example.retailplatform.user.domain;

public class UserConstants {
    // Existing keys
    public static final String USER_NOT_FOUND_KEY = "user.not.found";
    public static final String USER_ALREADY_EXISTS_KEY = "user.already.exists"; // fallback
    public static final String VALIDATION_FAILED_KEY = "validation.failed";
    public static final String CONSTRAINT_VIOLATION_KEY = "constraint.violation";
    public static final String PERSISTENCE_EXCEPTION_KEY = "persistence.exception";
    public static final String RUNTIME_EXCEPTION_KEY = "runtime.exception";

    // New keys for field-specific uniqueness
    public static final String USERNAME_ALREADY_EXISTS_KEY = "username.already.exists";
    public static final String EMAIL_ALREADY_EXISTS_KEY = "email.already.exists";
    public static final String PHONE_ALREADY_EXISTS_KEY = "phone.already.exists";

    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE = "phoneNumber";

    public static final String REQUEST_URI = "/api/users";
    public static final String MESSAGE_PLACEHOLDER = "mockMessage";
}
