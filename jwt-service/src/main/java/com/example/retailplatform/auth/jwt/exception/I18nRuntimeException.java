package com.example.retailplatform.auth.jwt.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
public abstract class I18nRuntimeException extends RuntimeException {
    private final String code;

    public I18nRuntimeException(String code) {
        super(code);
        this.code = code;
    }

    public String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}