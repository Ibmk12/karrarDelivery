package com.karrardelivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {

    private final MessageSource messageSource;

    @Autowired
    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(final String code, @Nullable final Object... args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public String getMessage(final String code) {
        return this.getMessage(code, null);
    }

    public String getMessage(final String code, final Locale locale, Object... args) {
        return this.messageSource.getMessage(code, args, locale);
    }
}
