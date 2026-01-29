package com.mailboom.api.domain.model.valueobjects;

import com.mailboom.api.domain.exception.EmailCannotBeNullException;
import com.mailboom.api.domain.exception.InvalidEmailException;

import java.util.regex.Pattern;

public record Email(String email) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    );

    public Email {
        if (email == null || email.isBlank()) {
            throw new EmailCannotBeNullException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
    }

    public static Email fromString(String email) {
        return new Email(email);
    }

    @Override
    public String toString() {
        return email;
    }
}
