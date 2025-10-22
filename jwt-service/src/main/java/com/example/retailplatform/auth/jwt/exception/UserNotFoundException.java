package com.example.retailplatform.auth.jwt.exception;

public class UserNotFoundException extends I18nRuntimeException {
    public UserNotFoundException() { super("user.not.found"); }
}