package com.example.retailplatform.auth.jwt.exception;

public class PasswordResetTokenInvalidException extends I18nRuntimeException {
    public PasswordResetTokenInvalidException() {
        super("auth.password_reset.token_invalid"); // i18n message key
    }
}