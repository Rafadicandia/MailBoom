package com.mailboom.api.application.common.exception;

public class ContactListSizeExceedsLimitException extends RuntimeException {
    public ContactListSizeExceedsLimitException(String message) {
        super(message);
    }
}
