package com.mailboom.api.domain.exception;

public class ContactListMustHaveNameException extends RuntimeException {
    public ContactListMustHaveNameException(String message) {
        super(message);
    }
}
