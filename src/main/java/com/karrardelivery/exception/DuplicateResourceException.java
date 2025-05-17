package com.karrardelivery.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {

    private final String code;

    public DuplicateResourceException(String message, String code) {
        super(message);
        this.code = code;
    }
}
