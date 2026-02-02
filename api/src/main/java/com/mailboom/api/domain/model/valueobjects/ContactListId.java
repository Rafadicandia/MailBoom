package com.mailboom.api.domain.model.valueobjects;

import com.mailboom.api.domain.exception.ContactListIdCannotBeNullException;

import java.util.UUID;

public record ContactListId(UUID value) {
    public ContactListId {
        if (value == null || value.toString().isEmpty()) {
            throw new ContactListIdCannotBeNullException("ContactList ID cannot be null or empty");
        }
    }

    public static ContactListId fromString(String id) {
        return new ContactListId(UUID.fromString(id));
    }

    public static ContactListId generate() {
        return new ContactListId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value().toString();
    }

    public UUID value() {
        return value;
    }
}

