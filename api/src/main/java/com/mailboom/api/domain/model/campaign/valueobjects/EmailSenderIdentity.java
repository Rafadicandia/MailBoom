package com.mailboom.api.domain.model.campaign.valueobjects;

public record EmailSenderIdentity(String clientName) {
    private static final String BRAND_SUFFIX = "@mailboom.email";

    public EmailSenderIdentity {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Client name cannot be null or blank");
        }

        if (clientName.length() > 50) {
            throw new IllegalArgumentException("Client name is too long");
        }
    }

    public String format() {
        return clientName.trim() + BRAND_SUFFIX;
    }


    public String value() {
        return format();
    }
}
