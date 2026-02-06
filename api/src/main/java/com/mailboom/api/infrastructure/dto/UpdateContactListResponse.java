package com.mailboom.api.infrastructure.dto;

public record UpdateContactListResponse(
        String id,
        String name,
        String ownerId,
        long totalContacts

) {
}
