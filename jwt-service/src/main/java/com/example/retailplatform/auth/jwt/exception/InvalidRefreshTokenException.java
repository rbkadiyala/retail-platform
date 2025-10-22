package com.example.retailplatform.auth.jwt.exception;

public class InvalidRefreshTokenException extends I18nRuntimeException {
    public InvalidRefreshTokenException() { super("invalid.refresh.token"); }
}