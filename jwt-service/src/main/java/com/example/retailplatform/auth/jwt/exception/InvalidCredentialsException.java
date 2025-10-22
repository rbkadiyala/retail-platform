package com.example.retailplatform.auth.jwt.exception;

public class InvalidCredentialsException extends I18nRuntimeException {
    public InvalidCredentialsException() { super("invalid.credentials"); }
}
