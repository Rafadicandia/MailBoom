package com.mailboom.api.infrastructure.dto;

public record ContactListDataResponse(
        String id,
        String name,
        String ownerId,
        long totalContacts
) {
}
