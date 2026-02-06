package com.mailboom.api.application.common.exception;

public class ContactListIdNotFoundException extends RuntimeException {
    public ContactListIdNotFoundException(String message) {
        super(message);
    }
}
