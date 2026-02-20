package com.mailboom.api.application.common.exception;

public class UserIsNotAdmiException extends RuntimeException {
    public UserIsNotAdmiException(String message) {
        super(message);
    }
}
