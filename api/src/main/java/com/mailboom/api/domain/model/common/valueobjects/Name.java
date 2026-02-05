package com.mailboom.api.domain.model.common.valueobjects;

public record Name(String name) {
    public Name {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() >
                255) {
            throw new IllegalArgumentException("Name cannot be longer than 255 characters");
        }

    }

    public static Name fromString(String name) {
        return new Name(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public String value() {
        return name;
    }
}
