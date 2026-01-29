package com.mailboom.api.domain.exception;

public class UserCannotBeNullException extends RuntimeException {
    public UserCannotBeNullException(String message) {
        super(message);
    }
}
