package com.mailboom.api.domain.entity.valueobjects;

import com.mailboom.api.domain.exception.PasswordCannotBeNullException;

public record PasswordHash(String hash) {
    public PasswordHash {
        if (hash == null || hash.isBlank()) {
            throw new PasswordCannotBeNullException("Password hash cannot be null or empty");
        }
    }
    public static PasswordHash fromString(String hash) {
        return new PasswordHash(hash);
    }
    @Override
    public String toString() {
        return hash;
    }

}
