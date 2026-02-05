package com.mailboom.api.application.common.exception;

public class UserWithEmailAlreadyExistsException extends RuntimeException {
    public UserWithEmailAlreadyExistsException(String message) {
        super(message);
    }
}
