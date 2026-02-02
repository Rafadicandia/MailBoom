package com.mailboom.api.domain.model.valueobjects;

import com.mailboom.api.domain.exception.SubjectFormatException;
import org.springframework.util.Assert;

import com.mailboom.api.domain.exception.SubjectFormatException;

public record Subject(String value) {
    private static final int MAX_LENGTH = 150;

    public Subject {
        if (value != null) {
            value = value.trim();
        }

        if (value == null || value.isBlank()) {
            throw new SubjectFormatException("Subject cannot be empty");
        }

        if (value.length() > MAX_LENGTH) {
            throw new SubjectFormatException("Subject must not be more than " + MAX_LENGTH + " characters");
        }

        if (value.contains("\n") || value.contains("\r")) {
            throw new SubjectFormatException("Subject cannot contain line breaks");
        }

        if (value.length() > 10 && value.equals(value.toUpperCase())) {
            throw new SubjectFormatException("Excessive use of uppercase is not allowed for anti-spam reasons");
        }
    }

}
