package com.mailboom.api.infrastructure.contact.dto;

public record ContactListDataResponse(
        String id,
        String name,
        String ownerId,
        long totalContacts
) {
}
