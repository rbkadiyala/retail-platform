package com.example.retailplatform.user.domain;

/**
 * Central constants for user domain, validation, and security.
 */
public final class UserConstants {

    // ---------------- Domain / Entity Errors ----------------
    public static final String USER_NOT_FOUND_KEY = "user.not.found";
    public static final String USER_ALREADY_EXISTS_KEY = "user.already.exists";

    public static final String USERNAME_ALREADY_EXISTS_KEY = "username.already.exists";
    public static final String EMAIL_ALREADY_EXISTS_KEY = "email.already.exists";
    public static final String PHONE_ALREADY_EXISTS_KEY = "phone.already.exists";

    public static final String VALIDATION_FAILED_KEY = "validation.failed";
    public static final String CONSTRAINT_VIOLATION_KEY = "constraint.violation";
    public static final String PERSISTENCE_EXCEPTION_KEY = "persistence.exception";
    public static final String RUNTIME_EXCEPTION_KEY = "runtime.exception";

    // ---------------- Security Errors ----------------
    public static final String ERROR_AUTH_FAILED = "auth.failed";
    public static final String ERROR_ACCESS_DENIED = "access.denied";
    public static final String ERROR_INTERNAL_SERVER = "internal.server.error";

    // ---------------- Field Names ----------------
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE = "phoneNumber";

    // ---------------- Misc ----------------
    public static final String REQUEST_URI = "/api/users";
    public static final String MESSAGE_PLACEHOLDER = "mockMessage";
    public static final String SYSTEM = "System";

    public static final String ERROR_METHOD_NOT_ALLOWED = "error.method.not.allowed";
    public static final String ERROR_UNSUPPORTED_MEDIA_TYPE = "error.unsupported.media.type";

    // Private constructor to prevent instantiation
    private UserConstants() {
        throw new UnsupportedOperationException("UserConstants is a utility class and should not be instantiated");
    }
}
