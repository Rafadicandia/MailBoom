package com.mailboom.api.application.exception;

public class ContactListAlreadyExistException extends RuntimeException {
    public ContactListAlreadyExistException(String message) {
        super(message);
    }
}
