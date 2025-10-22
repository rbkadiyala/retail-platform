package com.example.retailplatform.auth.jwt.exception;

public class PasswordChangeRequiredException extends I18nRuntimeException {
    public PasswordChangeRequiredException() { super("password.change.required"); }
}
