package com.mailboom.api.domain.exception;

public class ContactCannotBeNullException extends RuntimeException {
    public ContactCannotBeNullException(String message) {
        super(message);
    }
}
