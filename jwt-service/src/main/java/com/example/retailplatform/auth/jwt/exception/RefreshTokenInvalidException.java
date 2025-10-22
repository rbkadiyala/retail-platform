package com.example.retailplatform.auth.jwt.exception;

public class RefreshTokenInvalidException extends I18nRuntimeException {
    public RefreshTokenInvalidException() {
        super("auth.refresh_token.invalid"); // i18n message key
    }
}