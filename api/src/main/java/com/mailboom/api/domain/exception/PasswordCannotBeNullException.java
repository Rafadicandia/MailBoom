package com.mailboom.api.domain.exception;

public class PasswordCannotBeNullException extends RuntimeException {
    public PasswordCannotBeNullException(String message) {
        super(message);
    }
}
