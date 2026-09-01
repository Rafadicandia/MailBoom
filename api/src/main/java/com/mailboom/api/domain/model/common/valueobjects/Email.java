package com.mailboom.api.domain.model.common.valueobjects;

import com.mailboom.api.domain.exception.InvalidEmailException;

import java.util.regex.Pattern;

public record Email(String email) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    );

    public Email(String email) {
        String normalizedEmail = email != null ? email : "";
        if (!normalizedEmail.isBlank() && !EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new InvalidEmailException("Invalid email format: " + normalizedEmail);
        }
        this.email = normalizedEmail;
    }

    public static Email fromString(String email) {
        return new Email(email);
    }

    @Override
    public String toString() {
        return email;
    }
}
