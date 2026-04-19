package com.mailboom.api.domain.model.whatsapp.valueobjects;

public record AccessToken(String value) {
    public AccessToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be empty");
        }
    }

    @Override
    public String toString() {
        return "AccessToken{*******}";
    }
}
