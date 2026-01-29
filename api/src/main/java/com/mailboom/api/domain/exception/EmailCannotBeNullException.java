package com.mailboom.api.domain.exception;

public class EmailCannotBeNullException extends RuntimeException {
    public EmailCannotBeNullException(String message) {
        super(message);
    }
}
