package com.mailboom.api.domain.model.campaign.valueobjects;

import java.util.regex.Pattern;

public record EmailSenderIdentity(String clientName) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@mailboom\\.email$"
    );


    public EmailSenderIdentity {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Client name cannot be null or blank");
        }
        if (clientName.length() > 100) {
            throw new IllegalArgumentException("Client name is too long");
        }
        if (!EMAIL_PATTERN.matcher(clientName).matches()) {
            throw new IllegalArgumentException("Invalid email format:" + clientName);
        }

    }

    public String value() {
        return clientName;
    }
}
