package com.mailboom.api.infrastructure.contact.dto;

import java.util.Map;
import java.util.Objects;

public record UpdateContactRequest(
        String contactId,
        String contactListId,
        String email,
        String name,
        Map<String, Object> customFields,
        boolean subscribed
) {
    public UpdateContactRequest {
        Objects.requireNonNull(contactId, "contactId cannot be null");
        Objects.requireNonNull(contactListId, "contactListId cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        if (customFields == null) {
            customFields = Map.of();
        }
    }
}
