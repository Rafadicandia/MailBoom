package com.mailboom.api.domain.model.campaign.valueobjects;

import com.mailboom.api.domain.exception.HtmlContentExtensionException;

public record HtmlContent(String value) {
    private static final int MAX_LENGTH = 500_000;

    public HtmlContent {
        if (value == null || value.isBlank()) {
            throw new HtmlContentExtensionException("Content cannot be empty");
        }

        if (value.length() > MAX_LENGTH) {
            throw new HtmlContentExtensionException(
                    String.format("Content too long (%d bytes). Max allowed: %d", value.length(), MAX_LENGTH)
            );
        }

    }

    public boolean containsPlaceholder(String key) {
        return value != null && value.contains("{{" + key + "}}");
    }
}