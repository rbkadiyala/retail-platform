package com.example.retailplatform.auth.jwt.exception;

public class PasswordResetTokenExpiredException extends I18nRuntimeException {
    public PasswordResetTokenExpiredException() {
        super("auth.password_reset.token_expired"); // i18n message key
    }
}