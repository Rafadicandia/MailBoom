package com.mailboom.api.domain.exception;

public class ContactListIdCannotBeNullException extends RuntimeException {
    public ContactListIdCannotBeNullException(String message) {
        super(message);
    }
}
