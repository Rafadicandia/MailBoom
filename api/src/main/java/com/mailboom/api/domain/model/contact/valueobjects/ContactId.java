package com.mailboom.api.domain.model.contact.valueobjects;

import com.mailboom.api.domain.exception.ContactCannotBeNullException;

import java.util.UUID;

public record ContactId(UUID value) {
    public ContactId {
        if (value == null || value.toString().isEmpty()) {
            throw new ContactCannotBeNullException("Contact ID cannot be null or empty");
        }
    }
    public static ContactId generate() {
        return new ContactId(UUID.randomUUID());
    }
    public static ContactId fromString(String id) {

        return new ContactId(UUID.fromString(id));
    }
    @Override
    public String toString() {
        return value().toString();
    }
    public UUID value() {
        return value;
    }
}
