package com.mailboom.api.application.common.exception;

public class ContactListAlreadyExistException extends RuntimeException {
    public ContactListAlreadyExistException(String message) {
        super(message);
    }
}
